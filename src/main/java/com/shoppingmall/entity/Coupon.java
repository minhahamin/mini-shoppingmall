package com.shoppingmall.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;  // 쿠폰 코드
    
    @Column(nullable = false)
    private String name;  // 쿠폰 이름
    
    @Column(columnDefinition = "TEXT")
    private String description;  // 쿠폰 설명
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;  // 할인 유형 (PERCENTAGE: %, FIXED: 원)
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;  // 할인 값
    
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal minPurchaseAmount = BigDecimal.ZERO;  // 최소 구매 금액
    
    @Column(nullable = false)
    private Integer usageLimit;  // 사용 제한 횟수 (null이면 무제한)
    
    @Column(nullable = false)
    @Builder.Default
    private Integer usedCount = 0;  // 사용된 횟수
    
    @Column(nullable = false)
    private LocalDateTime validFrom;  // 유효 시작일
    
    @Column(nullable = false)
    private LocalDateTime validTo;  // 유효 종료일
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;  // 활성화 여부
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum DiscountType {
        PERCENTAGE,  // 퍼센트 할인
        FIXED        // 고정 금액 할인
    }
    
    // 할인 금액 계산
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (orderAmount.compareTo(minPurchaseAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        if (discountType == DiscountType.PERCENTAGE) {
            BigDecimal discount = orderAmount.multiply(discountValue).divide(new BigDecimal("100"));
            // 할인 금액이 주문 금액을 초과하지 않도록
            return discount.compareTo(orderAmount) > 0 ? orderAmount : discount;
        } else {
            // 고정 금액 할인 (주문 금액을 초과하지 않도록)
            return discountValue.compareTo(orderAmount) > 0 ? orderAmount : discountValue;
        }
    }
    
    // 쿠폰 사용 가능 여부 확인
    public boolean isUsable() {
        LocalDateTime now = LocalDateTime.now();
        return active 
            && now.isAfter(validFrom) 
            && now.isBefore(validTo)
            && (usageLimit == null || usedCount < usageLimit);
    }
}

