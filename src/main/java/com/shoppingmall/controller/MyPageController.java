package com.shoppingmall.controller;

import com.shoppingmall.dto.UserUpdateDto;
import com.shoppingmall.entity.User;
import com.shoppingmall.entity.UserCoupon;
import com.shoppingmall.entity.Wishlist;
import com.shoppingmall.service.CartService;
import com.shoppingmall.service.CouponService;
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
    private final CouponService couponService;
    
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
        
        // 보유 쿠폰 개수
        List<UserCoupon> coupons = couponService.getAllCouponsByUser(username);
        int usableCouponCount = (int) coupons.stream().filter(UserCoupon::isUsable).count();
        model.addAttribute("couponCount", usableCouponCount);
        
        return "mypage/index";
    }
    
    @GetMapping("/coupons")
    public String myCoupons(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        User currentUser = userService.getUserByUsername(authentication.getName());
        
        // 관리자인 경우 관리자 경로로 리다이렉트
        if ("ADMIN".equals(currentUser.getRole())) {
            return "redirect:/admin/coupons";
        }
        
        // 일반 회원인 경우: 자신이 받은 쿠폰 목록
        List<UserCoupon> userCoupons = couponService.getAllCouponsByUser(authentication.getName());
        model.addAttribute("coupons", userCoupons);
        
        return "mypage/coupons";
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

