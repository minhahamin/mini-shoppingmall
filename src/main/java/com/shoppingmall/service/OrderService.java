package com.shoppingmall.service;

import com.shoppingmall.entity.*;
import com.shoppingmall.repository.OrderRepository;
import com.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    
    // 주문 생성
    public Order createOrder(String username, List<CartItem> cartItems, String address, String phone) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("주문할 상품이 없습니다");
        }
        
        // 주문 번호 생성
        String orderNumber = generateOrderNumber();
        
        // 총 금액 계산
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 주문 생성
        Order order = Order.builder()
                .user(user)
                .orderNumber(orderNumber)
                .totalAmount(totalAmount)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(address)
                .phoneNumber(phone)
                .build();
        
        // 주문 항목 추가
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .productName(cartItem.getProduct().getName())
                    .price(cartItem.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();
            order.getItems().add(orderItem);
        }
        
        return orderRepository.save(order);
    }
    
    // 주문 번호 생성
    private String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = ThreadLocalRandom.current().nextInt(10000, 99999);
        return "ORD-" + date + "-" + random;
    }
    
    // 결제 완료 처리
    public void markAsPaid(Long orderId, String paymentIntentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다"));
        
        order.setStatus(Order.OrderStatus.PAID);
        order.setPaymentIntentId(paymentIntentId);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);
    }
    
    // Stripe Session ID로 주문 찾기
    @Transactional(readOnly = true)
    public Order findByStripeSessionId(String sessionId) {
        return orderRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다"));
    }
    
    // 사용자 주문 목록 조회
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    // 주문 상세 조회
    @Transactional(readOnly = true)
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다"));
    }
    
    // 주문 저장
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}

