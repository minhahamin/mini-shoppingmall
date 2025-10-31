package com.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatisticsDto {
    private LocalDate date;
    private BigDecimal totalAmount;
    private Long orderCount;
    
    // 월별 통계용
    public SalesStatisticsDto(String month, BigDecimal totalAmount, Long orderCount) {
        // "2024-01" 형식의 문자열을 파싱
        this.totalAmount = totalAmount;
        this.orderCount = orderCount;
    }
}

