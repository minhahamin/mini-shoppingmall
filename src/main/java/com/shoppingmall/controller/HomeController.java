package com.shoppingmall.controller;

import com.shoppingmall.entity.Product;
import com.shoppingmall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final ProductService productService;
    
    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            model.addAttribute("username", authentication.getName());
            
            // 관리자 여부 확인
            boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }
        
        // 최신 상품 표시
        List<Product> products = productService.getAvailableProducts();
        if (products.size() > 6) {
            products = products.subList(0, 6);
        }
        model.addAttribute("products", products);
        
        return "index";
    }
}

