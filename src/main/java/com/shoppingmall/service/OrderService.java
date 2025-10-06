package com.shoppingmall.service;

import com.shoppingmall.entity.*;
import com.shoppingmall.repository.OrderRepository;
import com.shoppingmall.repository.ProductRepository;
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
    private final ProductRepository productRepository;
    
    // 주문 생성 (재고 확인만, CartItem ID들 저장)
    public Order createOrder(String username, List<CartItem> cartItems, String address, String phone) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("주문할 상품이 없습니다");
        }
        
        // 재고 사전 확인
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (!product.getAvailable()) {
                throw new IllegalArgumentException(product.getName() + "은(는) 판매 중지된 상품입니다");
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException(product.getName() + "의 재고가 부족합니다 (남은 재고: " + product.getStock() + "개)");
            }
        }
        
        // 주문 번호 생성
        String orderNumber = generateOrderNumber();
        
        // 총 금액 계산
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // CartItem ID들을 문자열로 저장
        String cartItemIds = cartItems.stream()
                .map(item -> String.valueOf(item.getId()))
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        
        // 주문 생성
        Order order = Order.builder()
                .user(user)
                .orderNumber(orderNumber)
                .totalAmount(totalAmount)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(address)
                .phoneNumber(phone)
                .cartItemIds(cartItemIds)  // CartItem ID들 저장
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
    
    // 결제 완료 처리 및 재고 차감
    public void markAsPaid(Long orderId, String paymentIntentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다"));
        
        // 이미 결제 완료된 주문이면 재고 차감 스킵
        if (order.getStatus() == Order.OrderStatus.PAID) {
            return;
        }
        
        // 재고 차감
        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            
            // 재고 확인
            if (product.getStock() < orderItem.getQuantity()) {
                throw new IllegalArgumentException(product.getName() + "의 재고가 부족합니다");
            }
            
            // 재고 감소
            product.setStock(product.getStock() - orderItem.getQuantity());
            productRepository.save(product);
        }
        
        // 주문 상태 업데이트
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

