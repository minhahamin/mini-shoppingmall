package com.shoppingmall.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    
    private Long id;
    
    @NotBlank(message = "상품명은 필수입니다")
    private String name;
    
    private String description;
    
    @NotNull(message = "가격은 필수입니다")
    @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다")
    private BigDecimal price;
    
    @NotNull(message = "재고는 필수입니다")
    private Integer stock;
    
    private String imageUrl;
    
    private String category;
    
    private Boolean available;
}

