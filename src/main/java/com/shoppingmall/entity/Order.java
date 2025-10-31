package com.shoppingmall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String orderNumber;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
    
    @Column(name = "payment_intent_id")
    private String paymentIntentId;
    
    @Column(name = "stripe_session_id")
    private String stripeSessionId;
    
    @Column(nullable = false)
    private String shippingAddress;
    
    private String phoneNumber;
    
    @Column(name = "cart_item_ids", columnDefinition = "TEXT")
    private String cartItemIds;  // 주문한 장바구니 항목 ID들 (쉼표로 구분)
    
    @Column(name = "user_coupon_id")
    private Long userCouponId;  // 사용한 쿠폰 ID
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;  // 할인 금액
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime paidAt;
    
    @Column(name = "tracking_company")
    private String trackingCompany;  // 배송사 (CJ대한통운, 한진택배, 로젠택배 등)
    
    @Column(name = "tracking_number")
    private String trackingNumber;  // 송장번호
    
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;  // 배송 시작일시
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;  // 배송 완료일시
    
    public enum OrderStatus {
        PENDING,        // 대기중
        PAID,          // 결제완료
        PROCESSING,    // 처리중
        SHIPPED,       // 배송중
        DELIVERED,     // 배송완료
        CANCELLED      // 취소됨
    }
}

