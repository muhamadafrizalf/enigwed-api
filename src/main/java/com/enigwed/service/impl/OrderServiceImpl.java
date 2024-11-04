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
import com.enigwed.service.BonusPackageService;
import com.enigwed.service.ImageService;
import com.enigwed.service.OrderService;
import com.enigwed.service.WeddingPackageService;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
            Order order = orderRepository.findById(bookCode).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.ORDER_NOT_FOUND));
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
            log.error("Error during uploading payment imager: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> cancelOrder(String orderId) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> finishOrder(String orderId) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<OrderResponse> findOrderById(String id) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> acceptPayment(String orderId) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<OrderResponse> rejectPayment(String orderId) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findAllOrders() {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findOrdersByWeddingOrganizerId(String weddingOrganizerId) {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findOrdersByWeddingPackageId(String weddingPackageId) {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findOrdersByStatus(EStatus status) {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findOrdersByWeddingOrganizerIdAndStatus(String weddingOrganizerId, EStatus status) {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findOrdersByTransactionDateBetween(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findOwnOrders(JwtClaim userInfo) {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findOwnOrdersByStatus(JwtClaim userInfo, EStatus status) {
        return List.of();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse acceptOrder(JwtClaim userInfo, String orderId) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse rejectOrder(JwtClaim userInfo, String orderId) {
        return null;
    }
}
