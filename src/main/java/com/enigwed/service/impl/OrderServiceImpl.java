package com.enigwed.service.impl;

import com.enigwed.constant.*;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.*;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.OrderResponse;
import com.enigwed.entity.*;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.ValidationException;
import com.enigwed.repository.OrderRepository;
import com.enigwed.service.*;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WeddingPackageService weddingPackageService;
    private final ProductService productService;
    private final ImageService imageService;
    private final WeddingOrganizerService weddingOrganizerService;
    private final NotificationService notificationService;
    private final ValidationUtil validationUtil;

    private String generateBookCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder("ENW-");
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }
        return code.toString();
    }

    private Order saveOrder(Order order) {
        String generatedCode;
        do {
            generatedCode = generateBookCode();
        } while (orderRepository.existsByBookCode(generatedCode));
        order.setBookCode(generatedCode);
        return orderRepository.save(order);
    }

    private Order findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.ID_IS_REQUIRED);
        return orderRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.ORDER_NOT_FOUND));
    }

    private void validateUserAccess(JwtClaim userInfo, Order order) throws AccessDeniedException {
        String userCredentialId = order.getWeddingOrganizer().getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        }
        throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED);
    }

    private Review createReview(ReviewRequest reviewRequest, Order order) {
        Review review = Review.builder()
                .order(order)
                .weddingOrganizer(order.getWeddingOrganizer())
                .weddingPackage(order.getWeddingPackage())
                .rating(reviewRequest.getRating())
                .build();
        if (reviewRequest.getComment()!=null) {
            review.setComment(reviewRequest.getComment());
        }
        if (reviewRequest.getCustomerName() !=null && !reviewRequest.getCustomerName().isEmpty()) {
            review.setCustomerName(reviewRequest.getCustomerName());
        }
        return review;
    }

    private List<Order> filterResult(FilterRequest filter, List<Order> orderList) {
        if (filter.getWeddingOrganizerId() != null) {
            orderList = orderList.stream().filter(item -> item.getWeddingOrganizer().getId().equals(filter.getWeddingOrganizerId())).toList();
        }
        if (filter.getOrderStatus() != null) {
            orderList = orderList.stream().filter(item -> item.getStatus().equals(filter.getOrderStatus())).toList();
        }
        if (filter.getStartDate() != null) {
            orderList = orderList.stream().filter(item -> item.getTransactionDate().isAfter(filter.getStartDate())).toList();
        }
        if (filter.getEndDate() != null) {
            orderList = orderList.stream().filter(item -> item.getTransactionDate().isBefore(filter.getEndDate())).toList();
        }
        if (filter.getWeddingPackageId() != null) {
            orderList = orderList.stream().filter(item -> item.getWeddingPackage().getId().equals(filter.getWeddingPackageId())).toList();
        }
        return orderList;
    }

    private void sendNotificationWeddingOrganizer(ENotificationType type, Order order, String message) {
        Notification notification = Notification.builder()
                .channel(ENotificationChannel.SYSTEM)
                .type(type)
                .receiver(EReceiver.WEDDING_ORGANIZER)
                .receiverId(order.getWeddingOrganizer().getUserCredential().getId())
                .dataType(EDataType.ORDER)
                .dataId(order.getId())
                .message(message)
                .build();
        notificationService.createNotification(notification);
        /*

            Create notification for channel email

        */
    }

    @Override
    public Order loadOrderById(String id) {
        return findByIdOrThrow(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> createOrder(OrderRequest orderRequest) {
        try {
            // ValidationException
            validationUtil.validateAndThrow(orderRequest);

            Order order = new Order();

            order.setWeddingDate(orderRequest.getWeddingDate());

            Customer customer = Customer.builder()
                    .name(orderRequest.getCustomer().getName())
                    .email(orderRequest.getCustomer().getEmail())
                    .phone(orderRequest.getCustomer().getPhone())
                    .address(orderRequest.getCustomer().getAddress())
                    .build();
            order.setCustomer(customer);

            // ErrorResponse
            WeddingPackage weddingPackage = weddingPackageService.loadWeddingPackageById(orderRequest.getWeddingPackageId());
            order.setBasePrice(weddingPackage.getPrice());
            order.setTotalPrice(weddingPackage.getPrice());
            order.setWeddingPackage(weddingPackage);
            order.setWeddingOrganizer(weddingPackage.getWeddingOrganizer());

            List<OrderDetail> orderDetailList = new ArrayList<>();
            //// Bonus Products
            if (weddingPackage.getBonusDetails() != null && !weddingPackage.getBonusDetails().isEmpty()) {
                for (BonusDetail bonusDetail : weddingPackage.getBonusDetails()) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(bonusDetail.getProduct());
                    orderDetail.setPrice(bonusDetail.getProduct().getPrice());
                    orderDetail.setQuantity(bonusDetail.getQuantity());
                    orderDetail.setBonus(true);
                    orderDetailList.add(orderDetail);
                }
            }
            //// Additional Products
            List<AdditionalProduct> additionalProductList = orderRequest.getAdditionalProducts();
            if (additionalProductList != null && !additionalProductList.isEmpty()) {
                double additionalPrice = 0;
                for (AdditionalProduct additionalProduct : orderRequest.getAdditionalProducts()) {
                    // ErrorResponse
                    Product product = productService.loadProductById(additionalProduct.getProductId());

                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(product);
                    orderDetail.setPrice(product.getPrice());
                    orderDetail.setQuantity(additionalProduct.getQuantity());
                    orderDetail.setBonus(false);
                    orderDetailList.add(orderDetail);

                    additionalPrice += product.getPrice() * additionalProduct.getQuantity();
                }
                order.setTotalPrice(order.getTotalPrice() + additionalPrice);
            }
            order.setOrderDetails(orderDetailList);

            order = saveOrder(order);

            sendNotificationWeddingOrganizer(ENotificationType.ORDER_RECEIVED, order, Message.NEW_ORDER_RECEIVED(customer.getName()));

            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, Message.ORDER_CREATED);

        } catch (ValidationException e) {
            log.error("Validation error while creating order: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while creating order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<OrderResponse> findOrderByBookCode(String bookCode) {
        try {
            // ErrorResponse
            if (bookCode == null || bookCode.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.BOOKING_CODE_IS_REQUIRED);
            // ErrorResponse
            Order order = orderRepository.findByBookCode(bookCode).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.ORDER_NOT_FOUND));

            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, Message.ORDER_FOUND);

        } catch (ErrorResponse e) {
            log.error("Error while loading order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> payOrder(MultipartFile image, String orderId) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(orderId);
            // ErrorResponse
            if (image == null) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.UPDATE_FAILED, ErrorMessage.NO_PAYMENT_IMAGE_FOUND);
            // ErrorResponse
            Image paymentImage = imageService.createImage(image);
            // ErrorResponse
            if (order.getPaymentImage() != null) {
                imageService.deleteImage(order.getPaymentImage().getId());
            }
            order.setPaymentImage(paymentImage);
            order.setStatus(EStatus.CHECKING_PAYMENT);
            order = orderRepository.save(order);
            sendNotificationWeddingOrganizer(ENotificationType.CONFIRM_PAYMENT, order, Message.CONFIRM_PAYMENT(order.getCustomer().getName()));
            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, Message.ORDER_UPDATED);
        } catch (ErrorResponse e) {
            log.error("Error while uploading payment image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> cancelOrder(String orderId) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(orderId);
            order.setStatus(EStatus.CANCELED);
            order = orderRepository.save(order);

            OrderResponse response = OrderResponse.information(order);
            sendNotificationWeddingOrganizer(ENotificationType.ORDER_CANCELLED, order, Message.ORDER_CANCELED(order.getCustomer().getName()));
            return ApiResponse.success(response, Message.ORDER_UPDATED);
        } catch (ErrorResponse e) {
            log.error("Error while canceling order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> reviewOrder(String orderId, ReviewRequest reviewRequest) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(orderId);

            validationUtil.validateAndThrow(reviewRequest);

            Review review = createReview(reviewRequest, order);
            order.setReview(review);
            order.setReviewed(true);

            order = orderRepository.save(order);

            sendNotificationWeddingOrganizer(ENotificationType.ORDER_REVIEWED, order, Message.ORDER_FINISHED(order.getCustomer().getName()));

            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, Message.ORDER_UPDATED);
        } catch (ValidationException e) {
            log.error("Validation error while creating review: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, e.getErrors().get(0));
        }  catch (ErrorResponse e) {
            log.error("Error while creating review: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<OrderResponse> findOrderById(String id) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(id);
            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, Message.ORDER_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error while loading order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findAllOrders(FilterRequest filter, PagingRequest pagingRequest) {
        List<Order> orderList = orderRepository.findAll();
        if (orderList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);

        orderList = filterResult(filter, orderList);
        if (orderList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);

        List<OrderResponse> responses = orderList.stream().map(OrderResponse::simple).toList();
        return ApiResponse.success(responses, pagingRequest, Message.ORDER_FOUND);
    }

    @Override
    public ApiResponse<OrderResponse> findOwnOrderById(JwtClaim userInfo, String id) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(id);

            validateUserAccess(userInfo, order);

            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, Message.ORDER_FOUND);
        } catch (AccessDeniedException e) {
            log.error("Access denied while loading order: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        }  catch (ErrorResponse e) {
            log.error("Error while loading order: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<List<OrderResponse>> findOwnOrders(JwtClaim userInfo, FilterRequest filter, PagingRequest pagingRequest) {
        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

        List<Order> orderList = orderRepository.findByWeddingOrganizerId(wo.getId());
        if (orderList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);

        orderList = filterResult(filter, orderList);
        if (orderList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);

        List<OrderResponse> responses = orderList.stream().map(OrderResponse::simple).toList();
        return ApiResponse.success(responses, pagingRequest, Message.ORDER_FOUND);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> acceptOrder(JwtClaim userInfo, String orderId) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(orderId);
            // AccessDeniedException
            validateUserAccess(userInfo, order);
            order.setStatus(EStatus.WAITING_FOR_PAYMENT);
            order = orderRepository.save(order);
            /*
             Send notification to customer
            */
            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, Message.ORDER_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied while accepting order: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while accepting order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> rejectOrder(JwtClaim userInfo, String orderId) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(orderId);

            // AccessDeniedException
            validateUserAccess(userInfo, order);

            order.setStatus(EStatus.REJECTED);
            order = orderRepository.save(order);
            /*
             Send notification to customer
            */
            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, Message.ORDER_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied while rejecting order: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while rejecting order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> confirmPayment(JwtClaim userInfo, String orderId) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(orderId);

            order.setStatus(EStatus.PAID);

            order = orderRepository.save(order);

            sendNotificationWeddingOrganizer(ENotificationType.ORDER_PAID, order, Message.ORDER_PAID(order.getCustomer().getName()));

            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, Message.ORDER_FOUND);

        } catch (ErrorResponse e) {
            log.error("Error while accepting order payment: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<OrderResponse> finishOrder(JwtClaim userInfo, String orderId) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(orderId);

            // AccessDeniedException
            validateUserAccess(userInfo, order);

            order.setStatus(EStatus.FINISHED);

            WeddingPackage weddingPackage = weddingPackageService.addOrderCount(order.getWeddingPackage());
            order.setWeddingPackage(weddingPackage);

            order = orderRepository.save(order);

            sendNotificationWeddingOrganizer(ENotificationType.ORDER_FINISHED, order, Message.ORDER_PAID(order.getCustomer().getName()));

            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, Message.ORDER_FOUND);

        } catch (AccessDeniedException e) {
            log.error("Access denied while finishing order: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while finishing order: {}", e.getError());
            throw e;
        }
    }
}
