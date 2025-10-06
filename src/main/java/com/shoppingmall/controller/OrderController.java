package com.shoppingmall.controller;

import com.shoppingmall.entity.Cart;
import com.shoppingmall.entity.CartItem;
import com.shoppingmall.entity.Order;
import com.shoppingmall.service.CartService;
import com.shoppingmall.service.OrderService;
import com.shoppingmall.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final CartService cartService;
    private final PaymentService paymentService;
    
    // 주문서 페이지
    @GetMapping("/checkout")
    public String checkoutPage(@RequestParam(required = false) List<Long> itemIds,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        Cart cart = cartService.getOrCreateCart(authentication.getName());
        List<CartItem> selectedItems;
        
        if (itemIds != null && !itemIds.isEmpty()) {
            // 선택된 항목만
            selectedItems = cart.getItems().stream()
                    .filter(item -> itemIds.contains(item.getId()))
                    .collect(Collectors.toList());
        } else {
            // 전체 항목
            selectedItems = cart.getItems();
        }
        
        if (selectedItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "주문할 상품을 선택해주세요");
            return "redirect:/cart";
        }
        
        model.addAttribute("items", selectedItems);
        model.addAttribute("total", selectedItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        model.addAttribute("stripePublicKey", paymentService.getPublicKey());
        
        return "order/checkout";
    }
    
    // 주문 생성 및 Stripe 결제 페이지로 리다이렉트
    @PostMapping("/create")
    public String createOrder(@RequestParam List<Long> itemIds,
                             @RequestParam String address,
                             @RequestParam String phone,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            Cart cart = cartService.getOrCreateCart(authentication.getName());
            
            // 선택된 항목만 가져오기
            List<CartItem> selectedItems = cart.getItems().stream()
                    .filter(item -> itemIds.contains(item.getId()))
                    .collect(Collectors.toList());
            
            if (selectedItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "주문할 상품을 선택해주세요");
                return "redirect:/cart";
            }
            
            // 주문 생성
            Order order = orderService.createOrder(authentication.getName(), selectedItems, address, phone);
            
            // Stripe 결제 세션 생성
            String baseUrl = "http://localhost:8080";
            Session session = paymentService.createCheckoutSession(
                    order,
                    baseUrl + "/order/success?session_id={CHECKOUT_SESSION_ID}",
                    baseUrl + "/order/cancel?orderId=" + order.getId()
            );
            
            // Order에 Stripe Session ID 저장 (중요!)
            order.setStripeSessionId(session.getId());
            orderService.saveOrder(order);
            
            // Stripe 결제 페이지로 리다이렉트
            return "redirect:" + session.getUrl();
            
        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("error", "결제 시스템 오류: " + e.getMessage());
            return "redirect:/cart";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "주문 생성 실패: " + e.getMessage());
            return "redirect:/cart";
        }
    }
    
    // 결제 성공
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("session_id") String sessionId,
                                 Authentication authentication,
                                 Model model) {
        try {
            Order order = orderService.findByStripeSessionId(sessionId);
            orderService.markAsPaid(order.getId(), sessionId);
            
            // 결제 완료 후 장바구니에서 해당 상품 제거
            cartService.clearCart(authentication.getName());
            
            model.addAttribute("order", order);
            return "order/success";
        } catch (Exception e) {
            model.addAttribute("error", "주문 정보를 불러오는데 실패했습니다");
            return "order/error";
        }
    }
    
    // 결제 취소
    @GetMapping("/cancel")
    public String paymentCancel(@RequestParam Long orderId,
                                RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "결제가 취소되었습니다");
        return "redirect:/cart";
    }
    
    // 주문 내역
    @GetMapping("/history")
    public String orderHistory(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        List<Order> orders = orderService.getUserOrders(authentication.getName());
        model.addAttribute("orders", orders);
        return "order/history";
    }
    
    // 주문 상세
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id,
                             Authentication authentication,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        try {
            Order order = orderService.getOrder(id);
            
            // 본인의 주문인지 확인
            if (!order.getUser().getUsername().equals(authentication.getName())) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다");
                return "redirect:/order/history";
            }
            
            model.addAttribute("order", order);
            return "order/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "주문을 찾을 수 없습니다");
            return "redirect:/order/history";
        }
    }
}

