package com.shoppingmall.controller;

import com.shoppingmall.entity.Product;
import com.shoppingmall.service.ProductService;
import com.shoppingmall.service.ReviewService;
import com.shoppingmall.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    private final WishlistService wishlistService;
    private final ReviewService reviewService;
    
    @GetMapping
    public String productList(@RequestParam(required = false) String search,
                             @RequestParam(required = false) String category,
                             Model model) {
        List<Product> products;
        
        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
            model.addAttribute("search", search);
        } else if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
            model.addAttribute("selectedCategory", category);
        } else {
            products = productService.getAvailableProducts();
        }
        
        model.addAttribute("products", products);
        return "products/list";
    }
    
    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, 
                               Authentication authentication, 
                               Model model) {
        Product product = productService.getProduct(id);
        model.addAttribute("product", product);
        
        // 리뷰 목록 추가
        model.addAttribute("reviews", reviewService.getReviewsByProduct(id));
        
        // 찜 여부 및 리뷰 작성 가능 여부 확인
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isWished = wishlistService.isInWishlist(authentication.getName(), id);
            model.addAttribute("isWished", isWished);
            
            // 해당 상품을 주문했는지 확인 (리뷰 작성 가능 여부)
            boolean canReview = reviewService.hasOrderedProduct(authentication.getName(), id);
            model.addAttribute("canReview", canReview);
            
            // 리뷰 작성 가능한 주문 항목 목록
            if (canReview) {
                model.addAttribute("reviewableOrderItems", 
                        reviewService.getReviewableOrderItemsForProduct(authentication.getName(), id));
            }
        }
        
        return "products/detail";
    }
}

