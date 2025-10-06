package com.shoppingmall.controller;

import com.shoppingmall.entity.Wishlist;
import com.shoppingmall.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    
    private final WishlistService wishlistService;
    
    // 찜 목록 보기
    @GetMapping
    public String viewWishlist(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        List<Wishlist> wishlist = wishlistService.getUserWishlist(authentication.getName());
        model.addAttribute("wishlist", wishlist);
        
        return "wishlist/view";
    }
    
    // 찜 추가
    @PostMapping("/add/{productId}")
    public String addToWishlist(@PathVariable Long productId,
                               Authentication authentication,
                               @RequestParam(required = false) String returnUrl,
                               RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            wishlistService.addToWishlist(authentication.getName(), productId);
            redirectAttributes.addFlashAttribute("success", "찜 목록에 추가되었습니다");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return returnUrl != null ? "redirect:" + returnUrl : "redirect:/products/" + productId;
    }
    
    // 찜 제거
    @PostMapping("/remove/{productId}")
    public String removeFromWishlist(@PathVariable Long productId,
                                    Authentication authentication,
                                    @RequestParam(required = false) String returnUrl,
                                    RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            wishlistService.removeFromWishlist(authentication.getName(), productId);
            redirectAttributes.addFlashAttribute("success", "찜 목록에서 제거되었습니다");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return returnUrl != null ? "redirect:" + returnUrl : "redirect:/wishlist";
    }
}

