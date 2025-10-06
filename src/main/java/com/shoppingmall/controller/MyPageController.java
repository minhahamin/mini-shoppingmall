package com.shoppingmall.controller;

import com.shoppingmall.dto.UserUpdateDto;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    
    @GetMapping("/edit")
    public String editProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User user = userService.getUserByUsername(authentication.getName());
        model.addAttribute("user", user);
        
        return "mypage/edit";
    }
    
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute UserUpdateDto updateDto,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            userService.updateUser(authentication.getName(), updateDto);
            redirectAttributes.addFlashAttribute("success", "회원정보가 수정되었습니다");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/mypage/edit";
        }
        
        return "redirect:/mypage";
    }
    
    @GetMapping("/delete")
    public String deleteAccountPage(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        return "mypage/delete";
    }
    
    @PostMapping("/delete/confirm")
    public String deleteAccount(@RequestParam String password,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes,
                               jakarta.servlet.http.HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            userService.deleteUser(authentication.getName(), password);
            
            // 세션 무효화
            request.getSession().invalidate();
            
            redirectAttributes.addFlashAttribute("success", "회원 탈퇴가 완료되었습니다. 그동안 이용해주셔서 감사합니다.");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/mypage/delete";
        }
    }
}

