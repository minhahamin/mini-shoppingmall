package com.shoppingmall.controller;

import com.shoppingmall.entity.Cart;
import com.shoppingmall.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    // 장바구니 보기
    @GetMapping
    public String viewCart(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        Cart cart = cartService.getOrCreateCart(authentication.getName());
        BigDecimal total = cartService.getCartTotal(cart);
        
        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        
        return "cart/view";
    }
    
    // 장바구니에 상품 추가
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                           @RequestParam(defaultValue = "1") Integer quantity,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            cartService.addToCart(authentication.getName(), productId, quantity);
            redirectAttributes.addFlashAttribute("success", "장바구니에 추가되었습니다");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/products/" + productId;
    }
    
    // 장바구니 항목 수량 변경
    @PostMapping("/update/{itemId}")
    public String updateQuantity(@PathVariable Long itemId,
                                 @RequestParam Integer quantity,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.updateQuantity(itemId, quantity);
            redirectAttributes.addFlashAttribute("success", "수량이 변경되었습니다");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    // 장바구니 항목 삭제
    @PostMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable Long itemId,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.removeFromCart(itemId);
            redirectAttributes.addFlashAttribute("success", "상품이 삭제되었습니다");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    // 장바구니 비우기
    @PostMapping("/clear")
    public String clearCart(Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            cartService.clearCart(authentication.getName());
            redirectAttributes.addFlashAttribute("success", "장바구니가 비워졌습니다");
        }
        
        return "redirect:/cart";
    }
}

