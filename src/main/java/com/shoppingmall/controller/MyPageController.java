package com.shoppingmall.controller;

import com.shoppingmall.entity.User;
import com.shoppingmall.entity.Wishlist;
import com.shoppingmall.service.CartService;
import com.shoppingmall.service.OrderService;
import com.shoppingmall.service.UserService;
import com.shoppingmall.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    
    private final UserService userService;
    private final WishlistService wishlistService;
    private final CartService cartService;
    private final OrderService orderService;
    
    @GetMapping
    public String mypage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = authentication.getName();
        
        // 사용자 정보
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        
        // 찜 목록 개수
        List<Wishlist> wishlist = wishlistService.getUserWishlist(username);
        model.addAttribute("wishlistCount", wishlist.size());
        
        // 장바구니 개수
        int cartItemCount = cartService.getCartItemCount(username);
        model.addAttribute("cartItemCount", cartItemCount);
        
        // 결제 완료된 주문 개수만 카운트
        int orderCount = orderService.getUserPaidOrders(username).size();
        model.addAttribute("orderCount", orderCount);
        
        return "mypage/index";
    }
}

