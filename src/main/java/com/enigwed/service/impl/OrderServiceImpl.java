package com.enigwed.service.impl;

import com.enigwed.constant.EStatus;
import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.OrderDetailRequest;
import com.enigwed.dto.request.OrderRequest;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WeddingPackageService weddingPackageService;
    private final BonusPackageService bonusPackageService;
    private final ImageService imageService;
    private final WeddingOrganizerService weddingOrganizerService;
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
                    .build();
            order.setCustomer(customer);
            // ErrorResponse
            WeddingPackage weddingPackage = weddingPackageService.loadWeddingPackageById(orderRequest.getWeddingPackageId());
            order.setWeddingPackageBasePrice(weddingPackage.getBasePrice());
            order.setWeddingPackage(weddingPackage);
            order.setWeddingOrganizer(weddingPackage.getWeddingOrganizer());
            List<OrderDetail> orderDetails = new ArrayList<>();
            if (orderRequest.getOrderDetails() != null && !orderRequest.getOrderDetails().isEmpty()) {
                for (OrderDetailRequest orderDetailRequest : orderRequest.getOrderDetails()) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    BonusPackage bonusPackage = bonusPackageService.loadBonusPackageById(orderDetailRequest.getBonusPackageId());
                    orderDetail.setBonusPackage(bonusPackage);
                    orderDetail.setPrice(bonusPackage.getPrice());
                    orderDetail.setQuantity(orderDetailRequest.getQuantity());
                    orderDetails.add(orderDetail);
                }
            }
            order.setOrderDetails(orderDetails);
            order = saveOrder(order);
            OrderResponse response = OrderResponse.from(order);
            return ApiResponse.success(response, Message.ORDER_CREATED);
        } catch (ValidationException e) {
            log.error("Validation error during creating order: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error during creating order: {}", e.getError());
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
            OrderResponse response = OrderResponse.from(order);
            return ApiResponse.success(response, Message.ORDER_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading order: {}", e.getError());
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
            if (image == null) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.UPDATE_FAILED, ErrorMessage.IMAGE_IS_NULL);
            // ErrorResponse
            Image paymentImage = imageService.createImage(image);
            // ErrorResponse
            if (order.getPaymentImage() != null) {
                imageService.deleteImage(order.getPaymentImage().getId());
            }
            order.setPaymentImage(paymentImage);
            order = orderRepository.save(order);
            OrderResponse response = OrderResponse.from(order);
            return ApiResponse.success(response, Message.ORDER_UPDATED);
        } catch (ErrorResponse e) {
            log.error("Error during uploading payment image: {}", e.getError());
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
            OrderResponse response = OrderResponse.from(order);
            return ApiResponse.success(response, Message.ORDER_UPDATED);
        } catch (ErrorResponse e) {
            log.error("Error during canceling order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> finishOrder(String orderId) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(orderId);
            /*
                ADD REVIEW
            */
            order.setStatus(EStatus.FINISHED);
            order = orderRepository.save(order);
            OrderResponse response = OrderResponse.from(order);
            return ApiResponse.success(response, Message.ORDER_UPDATED);
        } catch (ErrorResponse e) {
            log.error("Error during finishing order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<OrderResponse> findOrderById(String id) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(id);
            OrderResponse response = OrderResponse.from(order);
            return ApiResponse.success(response, Message.ORDER_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading order: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> confirmPayment(String orderId) {
        try {
            // ErrorResponse
            Order order = findByIdOrThrow(orderId);
            // ErrorMessage
            if (order.getPaymentImage() == null) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.UPDATE_FAILED, ErrorMessage.NO_PAYMENT_IMAGE_FOUND);
            order.setStatus(EStatus.PAID);
            order = orderRepository.save(order);
            OrderResponse response = OrderResponse.from(order);
            return ApiResponse.success(response, Message.ORDER_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during accepting order payment: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findAllOrders() {
        List<Order> orderList = orderRepository.findAll();
        if (orderList.isEmpty()) {
            return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);
        }
        List<OrderResponse> responses = orderList.stream().map(OrderResponse::from).toList();
        return ApiResponse.success(responses, Message.ORDER_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findOrdersByWeddingOrganizerId(String weddingOrganizerId) {
        List<Order> orderList = orderRepository.findByWeddingOrganizerId(weddingOrganizerId);
        if (orderList.isEmpty()) {
            return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);
        }
        List<OrderResponse> responses = orderList.stream().map(OrderResponse::from).toList();
        return ApiResponse.success(responses, Message.ORDER_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findOrdersByWeddingPackageId(String weddingPackageId) {
        List<Order> orderList = orderRepository.findByWeddingPackageId(weddingPackageId);
        if (orderList.isEmpty()) {
            return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);
        }
        List<OrderResponse> responses = orderList.stream().map(OrderResponse::from).toList();
        return ApiResponse.success(responses, Message.ORDER_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findOrdersByStatus(EStatus status) {
        List<Order> orderList = orderRepository.findByStatus(status);
        if (orderList.isEmpty()) {
            return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);
        }
        List<OrderResponse> responses = orderList.stream().map(OrderResponse::from).toList();
        return ApiResponse.success(responses, Message.ORDER_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findOrdersByWeddingOrganizerIdAndStatus(String weddingOrganizerId, EStatus status) {
        List<Order> orderList = orderRepository.findByWeddingOrganizerIdAndStatus(weddingOrganizerId, status);
        if (orderList.isEmpty()) {
            return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);
        }
        List<OrderResponse> responses = orderList.stream().map(OrderResponse::from).toList();
        return ApiResponse.success(responses, Message.ORDER_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findOrdersByTransactionDateBetween(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(LocalDateTime.now())) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.INVALID_START_DATE);
        if(end.isBefore(start)) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.INVALID_END_DATE);
        List<Order> orderList = orderRepository.findByTransactionDateBetween(start, end);
        if (orderList.isEmpty()) {
            return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);
        }
        List<OrderResponse> responses = orderList.stream().map(OrderResponse::from).toList();
        return ApiResponse.success(responses, Message.ORDER_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findOwnOrders(JwtClaim userInfo) {
        try {
            // ErrorResponse
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
            List<Order> orderList = orderRepository.findByWeddingOrganizerId(wo.getId());
            if (orderList.isEmpty()) {
                return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);
            }
            List<OrderResponse> responses = orderList.stream().map(OrderResponse::from).toList();
            return ApiResponse.success(responses, Message.ORDER_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading own orders: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<List<OrderResponse>> findOwnOrdersByTransactionDateBetween(JwtClaim userInfo, LocalDateTime start, LocalDateTime end) {
        try {
            if (start.isAfter(LocalDateTime.now())) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.INVALID_START_DATE);
            if(end.isBefore(start)) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.INVALID_END_DATE);
            // ErrorResponse
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
            List<Order> orderList = orderRepository.findByWeddingOrganizerIdAndTransactionDateBetween(wo.getId(), start, end);
            if (orderList.isEmpty()) {
                return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);
            }
            List<OrderResponse> responses = orderList.stream().map(OrderResponse::from).toList();
            return ApiResponse.success(responses, Message.ORDER_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading own orders by transaction date between: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findOwnOrdersByStatus(JwtClaim userInfo, EStatus status) {
        try {
            // ErrorResponse
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
            List<Order> orderList = orderRepository.findByWeddingOrganizerIdAndStatus(wo.getId(), status);
            if (orderList.isEmpty()) {
                return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);
            }
            List<OrderResponse> responses = orderList.stream().map(OrderResponse::from).toList();
            return ApiResponse.success(responses, Message.ORDER_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading own orders by status: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<OrderResponse>> findOwnOrdersByWeddingPackageId(JwtClaim userInfo, String weddingPackageId) {
        try {
            // ErrorResponse
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
            List<Order> orderList = orderRepository.findByWeddingOrganizerIdAndWeddingPackageId(wo.getId(), weddingPackageId);
            if (orderList.isEmpty()) {
                return ApiResponse.success(new ArrayList<>(), Message.NO_ORDER_FOUND);
            }
            List<OrderResponse> responses = orderList.stream().map(OrderResponse::from).toList();
            return ApiResponse.success(responses, Message.ORDER_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading own orders by wedding package id: {}", e.getError());
            throw e;
        }
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
            OrderResponse response = OrderResponse.from(order);
            return ApiResponse.success(response, Message.ORDER_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied during accepting order: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during accepting order: {}", e.getError());
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
            OrderResponse response = OrderResponse.from(order);
            return ApiResponse.success(response, Message.ORDER_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied during rejecting order: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during rejecting order: {}", e.getError());
            throw e;
        }
    }
}
