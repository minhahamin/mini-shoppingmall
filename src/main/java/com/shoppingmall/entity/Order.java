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
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime paidAt;
    
    public enum OrderStatus {
        PENDING,        // 대기중
        PAID,          // 결제완료
        PROCESSING,    // 처리중
        SHIPPED,       // 배송중
        DELIVERED,     // 배송완료
        CANCELLED      // 취소됨
    }
}

