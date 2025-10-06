package com.shoppingmall.controller;

import com.shoppingmall.entity.Product;
import com.shoppingmall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public String productList(@RequestParam(required = false) String search, Model model) {
        List<Product> products;
        
        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
            model.addAttribute("search", search);
        } else {
            products = productService.getAvailableProducts();
        }
        
        model.addAttribute("products", products);
        return "products/list";
    }
    
    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProduct(id);
        model.addAttribute("product", product);
        return "products/detail";
    }
}

