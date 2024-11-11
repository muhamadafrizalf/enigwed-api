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
import com.enigwed.repository.spesification.SearchSpecifications;
import com.enigwed.service.*;
import com.enigwed.util.AccessValidationUtil;
import com.enigwed.util.StatisticUtil;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final AccessValidationUtil accessValidationUtil;
    private final StatisticUtil statisticUtil;

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
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.ORDER_ID_IS_REQUIRED);
        return orderRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.ORDER_NOT_FOUND(id)));
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

    private ApiResponse<List<OrderResponse>> getListApiResponse(FilterRequest filter, PagingRequest pagingRequest, List<Order> orderList) {
        /* COUNT ORDER BY STATUS */
        Map<String, Integer> countByStatus = statisticUtil.countOrderByStatus(orderList);
        if (orderList.isEmpty()) return ApiResponse.successOrderList(new ArrayList<>(), pagingRequest, SMessage.NO_ORDER_FOUND, countByStatus);

        /* FILTER RESULT */
        orderList = filterResult(filter, orderList);
        if (orderList.isEmpty()) return ApiResponse.successOrderList(new ArrayList<>(), pagingRequest, SMessage.NO_ORDER_FOUND, countByStatus);

        /* RE-COUNT ORDER BY STATUS */
        countByStatus = statisticUtil.countOrderByStatus(orderList);

        /* FILTER RESULT BY STATUS */
        orderList = filterByStatus(filter, orderList);
        if (orderList.isEmpty()) return ApiResponse.successOrderList(new ArrayList<>(), pagingRequest, SMessage.NO_ORDER_FOUND, countByStatus);

        /* MAP RESULT */
        List<OrderResponse> responses = orderList.stream().map(OrderResponse::card).toList();
        return ApiResponse.successOrderList(responses, pagingRequest, SMessage.ORDERS_FOUND(orderList.size()), countByStatus);
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

    private void validateAndUpdateOrderStatus(Order order, EStatus before, EStatus after, String message) {
        if (!order.getStatus().equals(before))
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, SErrorMessage.INVALID_ORDER_STATUS(before, message));
        order.setStatus(after);
    }

    private void validateOrderNotInStatusAndUpdate(Order order, EStatus before, EStatus after, String message) {
        if (order.getStatus().equals(before))
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, SErrorMessage.INVALID_ORDER_NOT_STATUS(before, message));
        order.setStatus(after);
    }

    @Transactional(readOnly = true)
    @Override
    public Order loadOrderById(String id) {
        try {
            /* FIND ORDER */
            return findByIdOrThrow(id);
        } catch (ErrorResponse e) {
            log.error("Error while loading order by ID: {}", e.getError());
            throw e;
        }
    }

    @Override
    public List<Order> loadFinishedOrderByWeddingOrganizerIdAndTransactionDateBetween(String weddingOrganizerId, LocalDateTime from, LocalDateTime to) {
        return orderRepository.findByWeddingOrganizerIdAndStatusAndTransactionDateBetweenOrderByTransactionDateDesc(weddingOrganizerId, EStatus.FINISHED, from, to);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> customerCreateOrder(OrderRequest orderRequest) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(orderRequest);

            /* CREATE ORDER */
            Order order = new Order();
            // Set wedding date
            order.setWeddingDate(orderRequest.getWeddingDate());

            /* CREATE AND SET CUSTOMER */
            String phone = orderRequest.getCustomer().getPhone().startsWith("08") ?
                    String.format("+628%s", orderRequest.getCustomer().getPhone().substring(2)) :
                    orderRequest.getCustomer().getPhone();
            Customer customer = Customer.builder()
                    .name(orderRequest.getCustomer().getName())
                    .email(orderRequest.getCustomer().getEmail())
                    .phone(phone)
                    .address(orderRequest.getCustomer().getAddress())
                    .build();
            order.setCustomer(customer);

            /* LOAD AND SET WEDDING PACKAGE ATTRIBUTE */
            // ErrorResponse //
            WeddingPackage weddingPackage = weddingPackageService.loadWeddingPackageById(orderRequest.getWeddingPackageId());
            order.setBasePrice(weddingPackage.getPrice());
            order.setTotalPrice(weddingPackage.getPrice());
            order.setWeddingPackage(weddingPackage);
            order.setWeddingOrganizer(weddingPackage.getWeddingOrganizer());

            /* CREATE ORDER DETAILS */
            List<OrderDetail> orderDetailList = new ArrayList<>();
            // Add bonus product (included in wedding package) bonus is true
            if (weddingPackage.getBonusDetails() != null && !weddingPackage.getBonusDetails().isEmpty()) {
                for (BonusDetail bonusDetail : weddingPackage.getBonusDetails()) {
                    /* CREATE AND ADD ORDER DETAIL */
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(bonusDetail.getProduct());
                    orderDetail.setPrice(bonusDetail.getProduct().getPrice());
                    orderDetail.setQuantity(bonusDetail.getQuantity());
                    orderDetail.setBonus(true);
                    orderDetailList.add(orderDetail);
                }
            }

            /* CREATE ADDITIONAL PRODUCT */
            // Add additional product (not included in wedding package) bonus is false
            List<AdditionalProduct> additionalProductList = orderRequest.getAdditionalProducts();
            if (additionalProductList != null && !additionalProductList.isEmpty()) {
                // To calculate additional price
                double additionalPrice = 0;
                for (AdditionalProduct additionalProduct : orderRequest.getAdditionalProducts()) {
                    /* LOAD PRODUCT */
                    // ErrorResponse //
                    Product product = productService.loadProductById(additionalProduct.getProductId());

                    /* CREATE AND ADD ORDER DETAIL */
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(product);
                    orderDetail.setPrice(product.getPrice());
                    orderDetail.setQuantity(additionalProduct.getQuantity());
                    orderDetail.setBonus(false);
                    orderDetailList.add(orderDetail);

                    // Calculate additional price
                    additionalPrice += product.getPrice() * additionalProduct.getQuantity();
                }
                /* SET ADDITIONAL PRICE */
                order.setTotalPrice(order.getTotalPrice() + additionalPrice);
            }

            /* SET ORDER DETAILS */
            order.setOrderDetails(orderDetailList);

            /* SAVE ORDER */
            order = saveOrder(order);

            /* SEND NOTIFICATION */
            sendNotificationWeddingOrganizer(ENotificationType.ORDER_RECEIVED, order, SNotificationMessage.NEW_ORDER_RECEIVED(customer.getName()));

            /* MAP RESPONSE */
            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, SMessage.ORDER_CREATED(order.getBookCode()));

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
    public ApiResponse<OrderResponse> customerFindOrderByBookCode(String bookCode) {
        try {
            /* FIND ORDER */
            // ErrorResponse //
            if (bookCode == null || bookCode.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.BOOKING_CODE_IS_REQUIRED);
            // ErrorResponse //
            Order order = orderRepository.findByBookCode(bookCode).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.ORDER_NOT_FOUND));

            /* MAP ORDER */
            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, SMessage.ORDER_FOUND_BOOK_CODE(bookCode));

        } catch (ErrorResponse e) {
            log.error("Error while finding order by book code: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> customerPayOrder(MultipartFile image, String orderId) {
        try {
            /* LOAD ORDER */
            // ErrorResponse //
            Order order = findByIdOrThrow(orderId);

            /* VALIDATE ORDER IN THE RIGHT STATUS AND UPDATE STATUS */
            // ErrorResponse //
            validateAndUpdateOrderStatus(order, EStatus.WAITING_FOR_PAYMENT, EStatus.CHECKING_PAYMENT, "pay");

            /* VALIDATE INPUT */
            // ErrorResponse //
            if (image == null) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.UPDATE_FAILED, SErrorMessage.NO_PAYMENT_IMAGE_FOUND);

            /* CREATE AND SAVE PAYMENT IMAGE */
            // ErrorResponse //
            Image paymentImage = imageService.createImage(image);
            // ErrorResponse //
            if (order.getPaymentImage() != null) {
                imageService.deleteImage(order.getPaymentImage().getId());
            }
            order.setPaymentImage(paymentImage);

            /* SAVE ORDER */
            order = orderRepository.save(order);

            /* SEND NOTIFICATION */
            sendNotificationWeddingOrganizer(ENotificationType.CONFIRM_PAYMENT, order, SNotificationMessage.ORDER_PAID(order.getCustomer().getName()));

            /* MAP RESPONSE*/
            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, SMessage.ORDER_PAYED(order.getBookCode()));

        } catch (ErrorResponse e) {
            log.error("Error while uploading payment image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> customerCancelOrder(String orderId) {
        try {
            /* LOAD ORDER */
            // ErrorResponse //
            Order order = findByIdOrThrow(orderId);

            /* VALIDATE ORDER IN THE RIGHT STATUS AND UPDATE STATUS */
            // ErrorResponse //
            validateOrderNotInStatusAndUpdate(order, EStatus.FINISHED, EStatus.CANCELED, "cancel");

            /* SAVE ORDER */
            order = orderRepository.save(order);

            /* SEND NOTIFICATION */
            sendNotificationWeddingOrganizer(ENotificationType.ORDER_CANCELLED, order, SNotificationMessage.ORDER_CANCELED(order.getCustomer().getName()));

            /* MAP ORDER */
            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, SMessage.ORDER_CANCELED(order.getBookCode()));

        } catch (ErrorResponse e) {
            log.error("Error while canceling order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> customerReviewOrder(String orderId, ReviewRequest reviewRequest) {
        try {
            /* LOAD ORDER */
            // ErrorResponse //
            Order order = findByIdOrThrow(orderId);

            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(reviewRequest);

            /* VALIDATE ORDER IN THE RIGHT STATUS AND UPDATE STATUS */
            // ErrorResponse //
            validateAndUpdateOrderStatus(order, EStatus.FINISHED, EStatus.FINISHED, "review");

            /* CREATE AND SAVE REVIEW */
            Review review = Review.builder()
                    .order(order)
                    .weddingOrganizer(order.getWeddingOrganizer())
                    .weddingPackage(order.getWeddingPackage())
                    .rating(reviewRequest.getRating())
                    .customerName(reviewRequest.getCustomerName() != null ? reviewRequest.getCustomerName() : "Anonymous")
                    .comment(reviewRequest.getComment() != null ? reviewRequest.getComment() : "")
                    .visiblePublic(reviewRequest.getVisiblePublic() != null ? reviewRequest.getVisiblePublic() : false)
                    .build();
            order.setReview(review);
            order.setReviewed(true);

            /* SAVE ORDER */
            order = orderRepository.save(order);

            /* SEND NOTIFICATION */
            sendNotificationWeddingOrganizer(ENotificationType.ORDER_REVIEWED, order, SNotificationMessage.ORDER_REVIEWED(order.getCustomer().getName()));

            /* MAP RESPONSE */
            OrderResponse response = OrderResponse.information(order);
            return ApiResponse.success(response, SMessage.ORDER_REVIEWED(order.getBookCode()));

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
    public ApiResponse<List<OrderResponse>> findOwnOrders(JwtClaim userInfo, FilterRequest filter, PagingRequest pagingRequest, String keyword) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(pagingRequest);

            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            /* FIND ORDERS */
            Sort sort = Sort.by(Sort.Order.desc("transactionDate"));
            Specification<Order> spec = SearchSpecifications.searchOrder(keyword);
            List<Order> orderList = orderRepository.findAll(spec, sort);
            orderList = orderList.stream().filter(order -> order.getWeddingOrganizer().getId().equals(wo.getId())).toList();

            /* MAP RESPONSE */
            return getListApiResponse(filter, pagingRequest, orderList);

        } catch (ValidationException e) {
            log.error("Validation error while finding own orders: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while finding own orders: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<OrderResponse> findOwnOrderById(JwtClaim userInfo, String id) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            Order order = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, order.getWeddingOrganizer());

            /* MAP RESPONSE */
            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, SMessage.ORDER_FOUND(id));

        }  catch (ValidationException e) {
            log.error("Validation error while finding own order by ID: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, e.getErrors().get(0));
        } catch (AccessDeniedException e) {
            log.error("Access denied while finding own order by ID: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.FETCHING_FAILED, e.getMessage());
        }  catch (ErrorResponse e) {
            log.error("Error while finding own order by ID: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> acceptOrder(JwtClaim userInfo, String orderId) {
        try {
            /* LOAD ORDER */
            // ErrorResponse //
            Order order = findByIdOrThrow(orderId);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, order.getWeddingOrganizer());

            /* VALIDATE ORDER IN THE RIGHT STATUS AND UPDATE STATUS */
            // ErrorResponse //
            validateAndUpdateOrderStatus(order, EStatus.PENDING, EStatus.WAITING_FOR_PAYMENT, "accept");

            /* SAVE ORDER */
            order = orderRepository.save(order);

            /*
                Send notification to customer
            */

            /* MAP ORDER */
            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, SMessage.ORDER_ACCEPTED(order.getBookCode()));

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
            /* LOAD ORDER */
            // ErrorResponse //
            Order order = findByIdOrThrow(orderId);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, order.getWeddingOrganizer());

            /* VALIDATE ORDER IN THE RIGHT STATUS AND UPDATE STATUS */
            // ErrorResponse //
            validateAndUpdateOrderStatus(order, EStatus.PENDING, EStatus.REJECTED, "reject");

            /* SAVE ORDER */
            order = orderRepository.save(order);

            /*
                Send notification to customer
            */

            /* MAP ORDER */
            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, SMessage.ORDER_REJECTED(order.getBookCode()));

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
            /* LOAD ORDER */
            // ErrorResponse //
            Order order = findByIdOrThrow(orderId);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, order.getWeddingOrganizer());

            /* VALIDATE ORDER IN THE RIGHT STATUS AND UPDATE STATUS */
            // ErrorResponse //
            validateAndUpdateOrderStatus(order, EStatus.CHECKING_PAYMENT, EStatus.PAID, "confirm payment");

            /* SAVE ORDER */
            order = orderRepository.save(order);

            /*
                Send notification to customer
            */

            /* MAP ORDER */
            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, SMessage.PAYMENT_CONFIRMED(order.getBookCode()));

        } catch (AccessDeniedException e) {
            log.error("Access denied while confirming payment: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while confirming payment: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<OrderResponse> finishOrder(JwtClaim userInfo, String orderId) {
        try {
            /* LOAD ORDER */
            // ErrorResponse //
            Order order = findByIdOrThrow(orderId);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, order.getWeddingOrganizer());

            /* VALIDATE ORDER IN THE RIGHT STATUS AND UPDATE STATUS */
            // ErrorResponse //
            validateAndUpdateOrderStatus(order, EStatus.PAID, EStatus.FINISHED, "finished");

            /* ADD WEDDING PACKAGE ORDER COUNT */
            weddingPackageService.addOrderCount(order.getWeddingPackage());

            /* SAVE ORDER */
            order = orderRepository.save(order);

            /*
                Send notification to customer
            */

            /* MAP ORDER */
            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, SMessage.ORDER_FINISHED(order.getBookCode()));

        } catch (AccessDeniedException e) {
            log.error("Access denied while finishing order: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while finishing order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findAllOrders(FilterRequest filter, PagingRequest pagingRequest, String keyword) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(pagingRequest);

            /* FIND ORDERS */
            Sort sort = Sort.by(Sort.Order.desc("transactionDate"));
            Specification<Order> spec = SearchSpecifications.searchOrder(keyword);
            List<Order> orderList = orderRepository.findAll(spec, sort);

            /* MAP RESPONSE */
            return getListApiResponse(filter, pagingRequest, orderList);

        } catch (ValidationException e) {
            log.error("Validation error while finding all orders: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while finding all orders: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<OrderResponse> findOrderById(String id) {
        try {
            /* FIND ORDER */
            // ErrorResponse //
            Order order = findByIdOrThrow(id);

            /* MAP ORDER */
            OrderResponse response = OrderResponse.all(order);
            return ApiResponse.success(response, SMessage.ORDER_FOUND(id));

        } catch (ErrorResponse e) {
            log.error("Error while finding order by ID: {}", e.getError());
            throw e;
        }
    }


}
