package com.shoppingmall.controller;

import com.shoppingmall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    
    private final ProductService productService;
    
    @ModelAttribute("categories")
    public List<String> categories() {
        return productService.getAllCategories();
    }
}

