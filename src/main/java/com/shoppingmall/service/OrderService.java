package com.shoppingmall.service;

import com.shoppingmall.entity.*;
import com.shoppingmall.repository.OrderRepository;
import com.shoppingmall.repository.ProductRepository;
import com.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
    public Order createOrder(String username, List<CartItem> cartItems, String address, String phone, Long userCouponId, BigDecimal discountAmount) {
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
        BigDecimal subtotal = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 할인 금액 적용 (할인 금액이 총액보다 크면 안됨)
        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }
        if (discountAmount.compareTo(subtotal) > 0) {
            discountAmount = subtotal;
        }
        
        BigDecimal totalAmount = subtotal.subtract(discountAmount);
        
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
                .userCouponId(userCouponId)  // 사용한 쿠폰 ID
                .discountAmount(discountAmount)  // 할인 금액
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
        
        // 재고 차감 및 판매량 증가
        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            
            // 재고 확인
            if (product.getStock() < orderItem.getQuantity()) {
                throw new IllegalArgumentException(product.getName() + "의 재고가 부족합니다");
            }
            
            // 재고 감소
            product.setStock(product.getStock() - orderItem.getQuantity());
            
            // 판매량 증가
            product.setSalesCount(product.getSalesCount() + orderItem.getQuantity());
            
            productRepository.save(product);
        }
        
        // 주문 상태 업데이트
        order.setStatus(Order.OrderStatus.PAID);
        order.setPaymentIntentId(paymentIntentId);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);
    }
    
    // 주문 생성 (쿠폰 없이)
    public Order createOrder(String username, List<CartItem> cartItems, String address, String phone) {
        return createOrder(username, cartItems, address, phone, null, BigDecimal.ZERO);
    }
    
    // Stripe Session ID로 주문 찾기
    @Transactional(readOnly = true)
    public Order findByStripeSessionId(String sessionId) {
        return orderRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다"));
    }
    
    // 사용자 주문 목록 조회 (모든 주문)
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    // 사용자 결제 완료 주문 목록 조회
    @Transactional(readOnly = true)
    public List<Order> getUserPaidOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        return orderRepository.findByUserAndStatusOrderByCreatedAtDesc(user, Order.OrderStatus.PAID);
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
    
    // 배송 정보 업데이트 (관리자용)
    public Order updateShippingInfo(Long orderId, String trackingCompany, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다"));
        
        order.setTrackingCompany(trackingCompany);
        order.setTrackingNumber(trackingNumber);
        
        // 송장번호가 입력되면 배송중 상태로 변경
        if (trackingNumber != null && !trackingNumber.isEmpty()) {
            order.setStatus(Order.OrderStatus.SHIPPED);
            order.setShippedAt(LocalDateTime.now());
        }
        
        return orderRepository.save(order);
    }
    
    // 배송 상태 업데이트 (API로 조회한 결과 반영)
    public Order updateDeliveryStatus(Long orderId, boolean isDelivered) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다"));
        
        if (isDelivered) {
            order.setStatus(Order.OrderStatus.DELIVERED);
            order.setDeliveredAt(LocalDateTime.now());
        } else {
            order.setStatus(Order.OrderStatus.SHIPPED);
        }
        
        return orderRepository.save(order);
    }
    
    // 모든 주문 조회 (관리자용)
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
    
    // 매출 통계 조회 (일별 - 최근 30일)
    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> getDailySalesStatistics(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        List<Object[]> results = orderRepository.findDailySales(
                Order.OrderStatus.PAID, startDate, endDate
        );
        
        return results.stream().map(row -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            // 날짜 형식 변환
            Object dateObj = row[0];
            String dateStr;
            if (dateObj instanceof java.sql.Date) {
                dateStr = dateObj.toString();
            } else if (dateObj instanceof java.time.LocalDate) {
                dateStr = dateObj.toString();
            } else {
                dateStr = dateObj.toString();
            }
            map.put("date", dateStr);
            map.put("total", ((BigDecimal) row[1]).setScale(0, java.math.RoundingMode.HALF_UP).longValue());
            map.put("count", ((Number) row[2]).longValue());
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    // 매출 통계 조회 (월별 - 최근 12개월)
    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> getMonthlySalesStatistics(int months) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months);
        
        List<Object[]> results = orderRepository.findMonthlySales(
                Order.OrderStatus.PAID.name(), startDate, endDate
        );
        
        return results.stream().map(row -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("month", row[0] != null ? row[0].toString() : "");
            
            // BigDecimal 또는 Number 타입 처리
            Object totalObj = row[1];
            long total;
            if (totalObj instanceof BigDecimal) {
                total = ((BigDecimal) totalObj).setScale(0, java.math.RoundingMode.HALF_UP).longValue();
            } else if (totalObj instanceof Number) {
                total = ((Number) totalObj).longValue();
            } else {
                total = 0;
            }
            map.put("total", total);
            
            map.put("count", row[2] != null ? ((Number) row[2]).longValue() : 0L);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    // 전체 매출액 조회
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return orderRepository.getTotalRevenue(Order.OrderStatus.PAID);
    }
    
    // 총 주문 수 조회
    @Transactional(readOnly = true)
    public Long getTotalOrderCount() {
        return orderRepository.countByStatus(Order.OrderStatus.PAID);
    }
    
    // 오늘 매출액
    @Transactional(readOnly = true)
    public BigDecimal getTodayRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        List<Object[]> results = orderRepository.findDailySales(
                Order.OrderStatus.PAID, startOfDay, endOfDay
        );
        
        if (results.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return ((BigDecimal) results.get(0)[1]);
    }
    
    // 이번 달 매출액
    @Transactional(readOnly = true)
    public BigDecimal getThisMonthRevenue() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        List<Object[]> results = orderRepository.findDailySales(
                Order.OrderStatus.PAID, startOfMonth, endOfMonth
        );
        
        return results.stream()
                .map(row -> (BigDecimal) row[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // 상태별 주문 통계
    @Transactional(readOnly = true)
    public java.util.Map<String, Long> getOrderStatisticsByStatus() {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            stats.put(status.name(), orderRepository.countByStatus(status));
        }
        return stats;
    }
}

