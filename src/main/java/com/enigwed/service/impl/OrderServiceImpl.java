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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.ID_IS_REQUIRED);
        return orderRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.ORDER_NOT_FOUND));
    }

    private void validateUserAccess(JwtClaim userInfo, Order order) throws AccessDeniedException {
        String userCredentialId = order.getWeddingOrganizer().getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        }
        throw new AccessDeniedException(SErrorMessage.ACCESS_DENIED);
    }

    private Review createReview(ReviewRequest reviewRequest, Order order) {
        return Review.builder()
                .order(order)
                .weddingOrganizer(order.getWeddingOrganizer())
                .weddingPackage(order.getWeddingPackage())
                .rating(reviewRequest.getRating())
                .customerName(reviewRequest.getCustomerName() != null ? reviewRequest.getCustomerName() : "Anonymous")
                .comment(reviewRequest.getComment() != null ? reviewRequest.getComment() : "")
                .visiblePublic(reviewRequest.getVisiblePublic() != null ? reviewRequest.getVisiblePublic() : true)
                .build();
    }

    private List<Order> filterResult(FilterRequest filter, List<Order> orderList) {
        return orderList.stream()
                .filter(item ->
                        (filter.getWeddingOrganizerId() == null || item.getWeddingOrganizer().getId().equals(filter.getWeddingOrganizerId())) &&
                                (filter.getStartDate() == null || !item.getTransactionDate().isBefore(filter.getStartDate())) &&
                                (filter.getEndDate() == null || !item.getTransactionDate().isAfter(filter.getEndDate())) &&
                                (filter.getWeddingPackageId() == null || item.getWeddingPackage().getId().equals(filter.getWeddingPackageId()))
                )
                .toList();
    }


    private List<Order> filterByStatus(FilterRequest filter, List<Order> orderList) {
        return orderList.stream()
                .filter(item -> filter.getOrderStatus() == null || item.getStatus().equals(filter.getOrderStatus()))
                .toList();
    }


    private Map<String, Integer> countByStatus(List<Order> orderList) {
        Map<String, Integer> map = new HashMap<>();
        map.put("ALL", 0);
        for (EStatus status : EStatus.values()) {
            map.put(status.name(), 0);
        }
        for (Order order : orderList) {
            map.put("ALL", map.get("ALL") + 1);
            map.put(order.getStatus().name(), map.get(order.getStatus().name()) + 1);
        }
        return map;
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

    private void validateOrderInStatus(Order order, EStatus status) {
        if (!order.getStatus().equals(status))
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, SErrorMessage.INVALID_ORDER_STATUS);
    }

    private void validateOrderNotInStatus(Order order, EStatus status) {
        if (order.getStatus().equals(status))
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, SErrorMessage.INVALID_ORDER_STATUS);
    }

    @Override
    public Order loadOrderById(String id) {
        return findByIdOrThrow(id);
    }

    @Override
    public List<Order> loadAllOrders(String weddingOrganizerId, LocalDateTime from, LocalDateTime to) {
        return orderRepository.findByWeddingOrganizerIdAndStatusAndTransactionDateBetween(weddingOrganizerId, EStatus.FINISHED, from, to);
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

            sendNotificationWeddingOrganizer(ENotificationType.ORDER_RECEIVED, order, SMessage.NEW_ORDER_RECEIVED(customer.getName()));

            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, SMessage.ORDER_CREATED);

        } catch (ValidationException e) {
            log.error("Validation error while creating order: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, e.getErrors().get(0));
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
            if (bookCode == null || bookCode.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.BOOKING_CODE_IS_REQUIRED);
            // ErrorResponse
            Order order = orderRepository.findByBookCode(bookCode).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.ORDER_NOT_FOUND));

            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, SMessage.ORDER_FOUND);

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
            if (image == null) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.UPDATE_FAILED, SErrorMessage.NO_PAYMENT_IMAGE_FOUND);

            /* VALIDATE ORDER IN THE RIGHT STATUS BEFORE UPDATING */
            // ErrorResponse
            validateOrderInStatus(order, EStatus.WAITING_FOR_PAYMENT);

            // ErrorResponse
            Image paymentImage = imageService.createImage(image);
            // ErrorResponse
            if (order.getPaymentImage() != null) {
                imageService.deleteImage(order.getPaymentImage().getId());
            }
            order.setPaymentImage(paymentImage);
            order.setStatus(EStatus.CHECKING_PAYMENT);
            order = orderRepository.save(order);
            sendNotificationWeddingOrganizer(ENotificationType.CONFIRM_PAYMENT, order, SMessage.CONFIRM_PAYMENT(order.getCustomer().getName()));
            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, SMessage.ORDER_UPDATED);
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

            /* VALIDATE ORDER IN THE RIGHT STATUS BEFORE UPDATING */
            // ErrorResponse
            validateOrderNotInStatus(order, EStatus.FINISHED);

            OrderResponse response = OrderResponse.information(order);
            sendNotificationWeddingOrganizer(ENotificationType.ORDER_CANCELLED, order, SMessage.ORDER_CANCELED(order.getCustomer().getName()));
            return ApiResponse.success(response, SMessage.ORDER_UPDATED);
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

            /* VALIDATE ORDER IN THE RIGHT STATUS BEFORE UPDATING */
            // ErrorResponse
            validateOrderInStatus(order, EStatus.FINISHED);

            Review review = createReview(reviewRequest, order);
            order.setReview(review);
            order.setReviewed(true);

            order = orderRepository.save(order);

            sendNotificationWeddingOrganizer(ENotificationType.ORDER_REVIEWED, order, SMessage.ORDER_FINISHED(order.getCustomer().getName()));

            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, SMessage.ORDER_UPDATED);
        } catch (ValidationException e) {
            log.error("Validation error while creating review: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, e.getErrors().get(0));
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
            return ApiResponse.success(response, SMessage.ORDER_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error while loading order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findAllOrders(FilterRequest filter, PagingRequest pagingRequest) {
        validationUtil.validateAndThrow(pagingRequest);

        List<Order> orderList = orderRepository.findAllByOrderByTransactionDateDesc();
        Map<String, Integer> countByStatus = countByStatus(orderList);
        if (orderList.isEmpty()) return ApiResponse.successOrderList(new ArrayList<>(), pagingRequest, SMessage.NO_ORDER_FOUND, countByStatus);

        orderList = filterResult(filter, orderList);
        if (orderList.isEmpty()) return ApiResponse.successOrderList(new ArrayList<>(), pagingRequest, SMessage.NO_ORDER_FOUND, countByStatus);

        countByStatus = countByStatus(orderList);
        orderList = filterByStatus(filter, orderList);
        if (orderList.isEmpty()) return ApiResponse.successOrderList(new ArrayList<>(), pagingRequest, SMessage.NO_ORDER_FOUND, countByStatus);

        List<OrderResponse> responses = orderList.stream().map(OrderResponse::simple).toList();
        return ApiResponse.successOrderList(responses, pagingRequest, SMessage.ORDER_FOUND, countByStatus);
    }

    @Override
    public ApiResponse<OrderResponse> findOwnOrderById(JwtClaim userInfo, String id) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(id);

            validateUserAccess(userInfo, order);

            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, SMessage.ORDER_FOUND);
        } catch (AccessDeniedException e) {
            log.error("Access denied while loading order: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        }  catch (ErrorResponse e) {
            log.error("Error while loading order: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<List<OrderResponse>> findOwnOrders(JwtClaim userInfo, FilterRequest filter, PagingRequest pagingRequest) {
        validationUtil.validateAndThrow(pagingRequest);

        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

        List<Order> orderList = orderRepository.findByWeddingOrganizerIdOrderByTransactionDateDesc(wo.getId());
        Map<String, Integer> countByStatus = countByStatus(orderList);
        if (orderList.isEmpty()) return ApiResponse.successOrderList(new ArrayList<>(), pagingRequest, SMessage.NO_ORDER_FOUND, countByStatus);

        orderList = filterResult(filter, orderList);
        if (orderList.isEmpty()) return ApiResponse.successOrderList(new ArrayList<>(), pagingRequest, SMessage.NO_ORDER_FOUND, countByStatus);

        countByStatus = countByStatus(orderList);
        orderList = filterByStatus(filter, orderList);
        if (orderList.isEmpty()) return ApiResponse.successOrderList(new ArrayList<>(), pagingRequest, SMessage.NO_ORDER_FOUND, countByStatus);

        List<OrderResponse> responses = orderList.stream().map(OrderResponse::simple).toList();
        return ApiResponse.successOrderList(responses, pagingRequest, SMessage.ORDER_FOUND, countByStatus);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> acceptOrder(JwtClaim userInfo, String orderId) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(orderId);
            // AccessDeniedException
            validateUserAccess(userInfo, order);

            /* VALIDATE ORDER IN THE RIGHT STATUS BEFORE UPDATING */
            // ErrorResponse
            validateOrderInStatus(order, EStatus.PENDING);

            order.setStatus(EStatus.WAITING_FOR_PAYMENT);
            order = orderRepository.save(order);
            /*
             Send notification to customer
            */
            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, SMessage.ORDER_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied while accepting order: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
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

            /* VALIDATE ORDER IN THE RIGHT STATUS BEFORE UPDATING */
            // ErrorResponse
            validateOrderInStatus(order, EStatus.PENDING);

            order.setStatus(EStatus.REJECTED);
            order = orderRepository.save(order);
            /*
             Send notification to customer
            */
            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, SMessage.ORDER_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied while rejecting order: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
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

            /* VALIDATE ORDER IN THE RIGHT STATUS BEFORE UPDATING */
            // ErrorResponse
            validateOrderInStatus(order, EStatus.PAID);

            order.setStatus(EStatus.PAID);

            order = orderRepository.save(order);

            sendNotificationWeddingOrganizer(ENotificationType.ORDER_PAID, order, SMessage.ORDER_PAID(order.getCustomer().getName()));

            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, SMessage.ORDER_FOUND);

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

            /* VALIDATE ORDER IN THE RIGHT STATUS BEFORE UPDATING */
            // ErrorResponse
            validateOrderInStatus(order, EStatus.PAID);

            order.setStatus(EStatus.FINISHED);

            WeddingPackage weddingPackage = weddingPackageService.addOrderCount(order.getWeddingPackage());
            order.setWeddingPackage(weddingPackage);

            order = orderRepository.save(order);

            sendNotificationWeddingOrganizer(ENotificationType.ORDER_FINISHED, order, SMessage.ORDER_PAID(order.getCustomer().getName()));

            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, SMessage.ORDER_FOUND);

        } catch (AccessDeniedException e) {
            log.error("Access denied while finishing order: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while finishing order: {}", e.getError());
            throw e;
        }
    }
}
