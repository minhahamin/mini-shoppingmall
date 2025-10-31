package com.shoppingmall.controller;

import com.shoppingmall.entity.Review;
import com.shoppingmall.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    
    /**
     * 리뷰 작성 페이지
     */
    @GetMapping("/new")
    public String createReviewForm(@RequestParam Long productId,
                                  @RequestParam(required = false) Long orderItemId,
                                  Authentication authentication,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다");
            return "redirect:/login";
        }
        
        try {
            // 주문한 상품인지 확인
            if (!reviewService.hasOrderedProduct(authentication.getName(), productId)) {
                redirectAttributes.addFlashAttribute("error", "주문한 상품에 대해서만 리뷰를 작성할 수 있습니다");
                return "redirect:/products/" + productId;
            }
            
            // 주문 항목이 있으면 리뷰 작성 가능 여부 확인
            if (orderItemId != null) {
                boolean canWrite = reviewService.canWriteReview(authentication.getName(), orderItemId);
                if (!canWrite) {
                    redirectAttributes.addFlashAttribute("error", "이미 리뷰를 작성하셨습니다");
                    return "redirect:/products/" + productId;
                }
            }
            
            model.addAttribute("productId", productId);
            model.addAttribute("orderItemId", orderItemId);
            return "review/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/" + productId;
        }
    }
    
    /**
     * 리뷰 작성
     */
    @PostMapping
    public String createReview(@RequestParam Long productId,
                              @RequestParam(required = false) Long orderItemId,
                              @RequestParam Integer rating,
                              @RequestParam String content,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다");
            return "redirect:/login";
        }
        
        try {
            reviewService.createReview(authentication.getName(), productId, orderItemId, rating, content);
            redirectAttributes.addFlashAttribute("success", "리뷰가 작성되었습니다");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "리뷰 작성 실패: " + e.getMessage());
        }
        
        return "redirect:/products/" + productId;
    }
    
    /**
     * 리뷰 수정 페이지
     */
    @GetMapping("/{id}/edit")
    public String editReviewForm(@PathVariable Long id,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다");
            return "redirect:/login";
        }
        
        try {
            Review review = reviewService.getReview(id);
            
            // 본인의 리뷰인지 확인
            if (!review.getUser().getUsername().equals(authentication.getName())) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다");
                return "redirect:/products/" + review.getProduct().getId();
            }
            
            model.addAttribute("review", review);
            return "review/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }
    
    /**
     * 리뷰 수정
     */
    @PostMapping("/{id}")
    public String updateReview(@PathVariable Long id,
                              @RequestParam Integer rating,
                              @RequestParam String content,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다");
            return "redirect:/login";
        }
        
        try {
            Review review = reviewService.updateReview(id, authentication.getName(), rating, content);
            redirectAttributes.addFlashAttribute("success", "리뷰가 수정되었습니다");
            return "redirect:/products/" + review.getProduct().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "리뷰 수정 실패: " + e.getMessage());
            return "redirect:/reviews/" + id + "/edit";
        }
    }
    
    /**
     * 리뷰 삭제
     */
    @PostMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다");
            return "redirect:/login";
        }
        
        try {
            Review review = reviewService.getReview(id);
            Long productId = review.getProduct().getId();
            
            reviewService.deleteReview(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "리뷰가 삭제되었습니다");
            return "redirect:/products/" + productId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "리뷰 삭제 실패: " + e.getMessage());
            return "redirect:/products";
        }
    }
    
    /**
     * 내 리뷰 목록
     */
    @GetMapping("/my")
    public String myReviews(Authentication authentication,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다");
            return "redirect:/login";
        }
        
        List<Review> reviews = reviewService.getReviewsByUser(authentication.getName());
        model.addAttribute("reviews", reviews);
        return "review/my-list";
    }
}

