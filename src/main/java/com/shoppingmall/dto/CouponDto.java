package com.shoppingmall.dto;

import com.shoppingmall.entity.Coupon;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponDto {
    
    private Long id;
    
    @NotBlank(message = "쿠폰 코드는 필수입니다")
    @Size(min = 3, max = 50, message = "쿠폰 코드는 3-50자 사이여야 합니다")
    private String code;
    
    @NotBlank(message = "쿠폰 이름은 필수입니다")
    @Size(max = 200, message = "쿠폰 이름은 최대 200자까지 입력 가능합니다")
    private String name;
    
    private String description;
    
    @NotNull(message = "할인 유형을 선택해주세요")
    private Coupon.DiscountType discountType;
    
    @NotNull(message = "할인 값을 입력해주세요")
    @DecimalMin(value = "0.01", message = "할인 값은 0보다 커야 합니다")
    private BigDecimal discountValue;
    
    @NotNull(message = "최소 구매 금액을 입력해주세요")
    @DecimalMin(value = "0", message = "최소 구매 금액은 0 이상이어야 합니다")
    private BigDecimal minPurchaseAmount = BigDecimal.ZERO;
    
    private Integer usageLimit;  // null이면 무제한
    
    @NotNull(message = "유효 시작일을 입력해주세요")
    private LocalDateTime validFrom;
    
    @NotNull(message = "유효 종료일을 입력해주세요")
    private LocalDateTime validTo;
    
    private Boolean active = true;
}

