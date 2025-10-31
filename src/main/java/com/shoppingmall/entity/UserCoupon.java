package com.shoppingmall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCoupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean used = false;  // 사용 여부
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;  // 사용 일시
    
    @Column(name = "order_id")
    private Long orderId;  // 사용한 주문 ID
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 쿠폰 사용 가능 여부 확인
    public boolean isUsable() {
        return !used && coupon != null && coupon.isUsable();
    }
    
    // 쿠폰 사용 처리
    public void markAsUsed(Long orderId) {
        this.used = true;
        this.usedAt = LocalDateTime.now();
        this.orderId = orderId;
        if (coupon != null) {
            coupon.setUsedCount(coupon.getUsedCount() + 1);
        }
    }
}

