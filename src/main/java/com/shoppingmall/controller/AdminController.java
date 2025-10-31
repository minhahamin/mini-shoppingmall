package com.shoppingmall.controller;

import com.shoppingmall.dto.CouponDto;
import com.shoppingmall.dto.ProductDto;
import com.shoppingmall.entity.Coupon;
import com.shoppingmall.entity.Product;
import com.shoppingmall.entity.User;
import com.shoppingmall.entity.Order;
import com.shoppingmall.service.CouponService;
import com.shoppingmall.service.DeliveryTrackingService;
import com.shoppingmall.service.FileStorageService;
import com.shoppingmall.service.OrderService;
import com.shoppingmall.service.ProductService;
import com.shoppingmall.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    
    private final ProductService productService;
    private final CouponService couponService;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final OrderService orderService;
    private final DeliveryTrackingService deliveryTrackingService;
    
    @GetMapping
    public String dashboard(Model model) {
        // 전체 매출액
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());
        
        // 총 주문 수
        model.addAttribute("totalOrderCount", orderService.getTotalOrderCount());
        
        // 오늘 매출액
        model.addAttribute("todayRevenue", orderService.getTodayRevenue());
        
        // 이번 달 매출액
        model.addAttribute("thisMonthRevenue", orderService.getThisMonthRevenue());
        
        // 상태별 주문 통계
        model.addAttribute("orderStatistics", orderService.getOrderStatisticsByStatus());
        
        // 일별 매출 통계 (최근 30일)
        model.addAttribute("dailySales", orderService.getDailySalesStatistics(30));
        
        // 월별 매출 통계 (최근 12개월)
        model.addAttribute("monthlySales", orderService.getMonthlySalesStatistics(12));
        
        // 최근 주문 목록 (최근 10개)
        List<Order> recentOrders = orderService.getAllOrders().stream()
                .limit(10)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("recentOrders", recentOrders);
        
        // 총 상품 수
        model.addAttribute("totalProductCount", productService.getAllProducts().size());
        
        // 총 회원 수
        model.addAttribute("totalUserCount", userService.getAllRegularUsers().size());
        
        return "admin/dashboard";
    }
    
    @GetMapping("/products")
    public String productList(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String search,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> productPage;
        
        if (search != null && !search.isEmpty()) {
            productPage = productService.searchProducts(search, pageable);
            model.addAttribute("search", search);
        } else {
            productPage = productService.getAllProducts(pageable);
        }
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("pageSize", size);
        
        return "admin/product-list";
    }
    
    @GetMapping("/products/new")
    public String createProductForm(Model model) {
        model.addAttribute("product", new ProductDto());
        model.addAttribute("isEdit", false);
        return "admin/product-form";
    }
    
    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute("product") ProductDto productDto,
                               BindingResult result,
                               @RequestParam(required = false) MultipartFile imageFile,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/product-form";
        }
        
        try {
            // 이미지 파일이 업로드된 경우 우선적으로 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = fileStorageService.storeFile(imageFile);
                productDto.setImageUrl(imageUrl);
            }
            // 이미지 파일이 없으면 productDto의 imageUrl을 그대로 사용 (URL 직접 입력 지원)
            
            productService.createProduct(productDto);
            redirectAttributes.addFlashAttribute("success", "상품이 등록되었습니다");
            return "redirect:/admin/products?page=" + page + "&size=" + size;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 등록에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/products/new?page=" + page + "&size=" + size;
        }
    }
    
    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        Product product = productService.getProduct(id);
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setStock(product.getStock());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setCategory(product.getCategory());
        productDto.setAvailable(product.getAvailable());
        
        model.addAttribute("product", productDto);
        model.addAttribute("isEdit", true);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "admin/product-form";
    }
    
    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id,
                               @Valid @ModelAttribute("product") ProductDto productDto,
                               BindingResult result,
                               @RequestParam(required = false) MultipartFile imageFile,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/product-form";
        }
        
        try {
            // 기존 상품 정보 가져오기
            Product existingProduct = productService.getProduct(id);
            String existingImageUrl = existingProduct.getImageUrl();
            
            // 새로운 이미지 파일이 업로드된 경우 처리
            if (imageFile != null && !imageFile.isEmpty()) {
                // 새 이미지 저장
                String newImageUrl = fileStorageService.storeFile(imageFile);
                productDto.setImageUrl(newImageUrl);
                
                // 기존 이미지가 서버에 저장된 파일인 경우 삭제
                if (existingImageUrl != null && existingImageUrl.startsWith("/uploads/")) {
                    fileStorageService.deleteFile(existingImageUrl);
                }
            } else {
                // 이미지 파일이 업로드되지 않은 경우 기존 imageUrl 유지
                productDto.setImageUrl(existingImageUrl);
            }
            
            productService.updateProduct(id, productDto);
            redirectAttributes.addFlashAttribute("success", "상품이 수정되었습니다");
            return "redirect:/admin/products?page=" + page + "&size=" + size;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/products/" + id + "/edit?page=" + page + "&size=" + size;
        }
    }
    
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               RedirectAttributes redirectAttributes) {
        try {
            // 삭제 전에 이미지 URL 가져오기
            Product product = productService.getProduct(id);
            String imageUrl = product.getImageUrl();
            
            // 상품 삭제
            productService.deleteProduct(id);
            
            // 서버에 저장된 이미지 파일이면 삭제
            if (imageUrl != null && imageUrl.startsWith("/uploads/")) {
                fileStorageService.deleteFile(imageUrl);
            }
            
            redirectAttributes.addFlashAttribute("success", "상품이 삭제되었습니다");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 삭제에 실패했습니다: " + e.getMessage());
        }
        return "redirect:/admin/products?page=" + page + "&size=" + size;
    }
    
    // ========== 쿠폰 관리 ==========
    
    @GetMapping("/coupons")
    public String couponList(@RequestParam(required = false) String search,
                            Model model) {
        List<Coupon> coupons = couponService.getAllCoupons();
        model.addAttribute("coupons", coupons);
        
        // 회원 목록도 함께 가져오기 (쿠폰 지급을 위해)
        List<User> users;
        if (search != null && !search.isEmpty()) {
            users = userService.getAllRegularUsers().stream()
                    .filter(user -> user.getUsername().contains(search) ||
                                   user.getName().contains(search) ||
                                   (user.getEmail() != null && user.getEmail().contains(search)))
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("search", search);
        } else {
            users = userService.getAllRegularUsers();
        }
        model.addAttribute("users", users);
        
        return "admin/coupon-list";
    }
    
    @GetMapping("/coupons/new")
    public String createCouponForm(Model model) {
        model.addAttribute("coupon", new CouponDto());
        model.addAttribute("isEdit", false);
        return "admin/coupon-form";
    }
    
    @PostMapping("/coupons")
    public String createCoupon(@Valid @ModelAttribute("coupon") CouponDto couponDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/coupon-form";
        }
        
        try {
            Coupon coupon = Coupon.builder()
                    .code(couponDto.getCode())
                    .name(couponDto.getName())
                    .description(couponDto.getDescription())
                    .discountType(couponDto.getDiscountType())
                    .discountValue(couponDto.getDiscountValue())
                    .minPurchaseAmount(couponDto.getMinPurchaseAmount())
                    .usageLimit(couponDto.getUsageLimit())
                    .validFrom(couponDto.getValidFrom())
                    .validTo(couponDto.getValidTo())
                    .active(couponDto.getActive())
                    .build();
            
            couponService.createCoupon(coupon);
            redirectAttributes.addFlashAttribute("success", "쿠폰이 생성되었습니다");
            return "redirect:/admin/coupons";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "쿠폰 생성에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/coupons/new";
        }
    }
    
    @GetMapping("/coupons/{id}/edit")
    public String editCouponForm(@PathVariable Long id, Model model) {
        Coupon coupon = couponService.getCoupon(id);
        CouponDto couponDto = new CouponDto();
        couponDto.setId(coupon.getId());
        couponDto.setCode(coupon.getCode());
        couponDto.setName(coupon.getName());
        couponDto.setDescription(coupon.getDescription());
        couponDto.setDiscountType(coupon.getDiscountType());
        couponDto.setDiscountValue(coupon.getDiscountValue());
        couponDto.setMinPurchaseAmount(coupon.getMinPurchaseAmount());
        couponDto.setUsageLimit(coupon.getUsageLimit());
        couponDto.setValidFrom(coupon.getValidFrom());
        couponDto.setValidTo(coupon.getValidTo());
        couponDto.setActive(coupon.getActive());
        
        model.addAttribute("coupon", couponDto);
        model.addAttribute("isEdit", true);
        return "admin/coupon-form";
    }
    
    @PostMapping("/coupons/{id}")
    public String updateCoupon(@PathVariable Long id,
                              @Valid @ModelAttribute("coupon") CouponDto couponDto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/coupon-form";
        }
        
        try {
            Coupon coupon = Coupon.builder()
                    .code(couponDto.getCode())
                    .name(couponDto.getName())
                    .description(couponDto.getDescription())
                    .discountType(couponDto.getDiscountType())
                    .discountValue(couponDto.getDiscountValue())
                    .minPurchaseAmount(couponDto.getMinPurchaseAmount())
                    .usageLimit(couponDto.getUsageLimit())
                    .validFrom(couponDto.getValidFrom())
                    .validTo(couponDto.getValidTo())
                    .active(couponDto.getActive())
                    .build();
            
            couponService.updateCoupon(id, coupon);
            redirectAttributes.addFlashAttribute("success", "쿠폰이 수정되었습니다");
            return "redirect:/admin/coupons";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "쿠폰 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/coupons/" + id + "/edit";
        }
    }
    
    @PostMapping("/coupons/{id}/delete")
    public String deleteCoupon(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            couponService.deleteCoupon(id);
            redirectAttributes.addFlashAttribute("success", "쿠폰이 삭제되었습니다");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "쿠폰 삭제에 실패했습니다: " + e.getMessage());
        }
        return "redirect:/admin/coupons";
    }
    
    // 쿠폰 지급 페이지
    @GetMapping("/coupons/{couponId}/issue")
    public String issueCouponPage(@PathVariable Long couponId,
                                  @RequestParam(required = false) String search,
                                  Model model) {
        Coupon coupon = couponService.getCoupon(couponId);
        List<User> users;
        
        if (search != null && !search.isEmpty()) {
            // 검색 기능 (아이디, 이름, 이메일로 검색)
            users = userService.getAllRegularUsers().stream()
                    .filter(user -> user.getUsername().contains(search) ||
                                   user.getName().contains(search) ||
                                   (user.getEmail() != null && user.getEmail().contains(search)))
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("search", search);
        } else {
            users = userService.getAllRegularUsers();
        }
        
        model.addAttribute("coupon", coupon);
        model.addAttribute("users", users);
        return "admin/coupon-issue";
    }
    
    // 회원에게 쿠폰 지급 (선택된 회원들)
    @PostMapping("/coupons/{couponId}/issue")
    public String issueCoupon(@PathVariable Long couponId,
                              @RequestParam(required = false) List<String> usernames,
                              @RequestParam(required = false, defaultValue = "false") Boolean issueToAll,
                              RedirectAttributes redirectAttributes) {
        try {
            if (issueToAll) {
                couponService.issueCouponToAllUsers(couponId);
                redirectAttributes.addFlashAttribute("success", "모든 회원에게 쿠폰이 지급되었습니다");
            } else if (usernames != null && !usernames.isEmpty()) {
                couponService.issueCouponToUsers(couponId, usernames);
                redirectAttributes.addFlashAttribute("success", usernames.size() + "명의 회원에게 쿠폰이 지급되었습니다");
            } else {
                redirectAttributes.addFlashAttribute("error", "회원을 선택해주세요");
                return "redirect:/admin/coupons/" + couponId + "/issue";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "쿠폰 지급에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/coupons/" + couponId + "/issue";
        }
        return "redirect:/admin/coupons";
    }
    
    // ========== 주문 관리 ==========
    
    @GetMapping("/orders")
    public String orderList(@RequestParam(required = false) String search,
                           @RequestParam(required = false) String status,
                           Model model) {
        List<Order> orders;
        
        if (search != null && !search.isEmpty()) {
            // 주문번호나 사용자명으로 검색
            orders = orderService.getAllOrders().stream()
                    .filter(order -> order.getOrderNumber().contains(search) ||
                                   order.getUser().getUsername().contains(search) ||
                                   (order.getTrackingNumber() != null && order.getTrackingNumber().contains(search)))
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("search", search);
        } else {
            orders = orderService.getAllOrders();
        }
        
        // 상태별 필터링
        if (status != null && !status.isEmpty()) {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
            orders = orders.stream()
                    .filter(order -> order.getStatus() == orderStatus)
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("statusFilter", status);
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("orderStatuses", Order.OrderStatus.values());
        return "admin/order-list";
    }
    
    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrder(id);
        model.addAttribute("order", order);
        
        // 배송 추적 URL 생성 (API 키 불필요)
        if (order.getTrackingCompany() != null && order.getTrackingNumber() != null) {
            String trackingUrl = deliveryTrackingService.getTrackingUrl(
                    order.getTrackingCompany(), 
                    order.getTrackingNumber()
            );
            model.addAttribute("trackingUrl", trackingUrl);
        }
        
        return "admin/order-detail";
    }
    
    @PostMapping("/orders/{id}/tracking")
    public String updateTrackingInfo(@PathVariable Long id,
                                    @RequestParam String trackingCompany,
                                    @RequestParam String trackingNumber,
                                    @RequestParam(required = false, defaultValue = "false") Boolean checkTracking,
                                    RedirectAttributes redirectAttributes) {
        try {
            // 배송 정보 업데이트
            orderService.updateShippingInfo(id, trackingCompany, trackingNumber);
            
            // 배송 추적 확인 요청 시 API 호출
            if (checkTracking && trackingNumber != null && !trackingNumber.isEmpty()) {
                try {
                    DeliveryTrackingService.DeliveryTrackingResult result = 
                            deliveryTrackingService.trackDelivery(trackingCompany, trackingNumber);
                    
                    // 배송 상태 업데이트
                    orderService.updateDeliveryStatus(id, result.isDelivered());
                    
                    if (result.isDelivered()) {
                        redirectAttributes.addFlashAttribute("success", 
                                "배송 정보가 업데이트되었습니다. 배송이 완료되었습니다.");
                    } else {
                        redirectAttributes.addFlashAttribute("success", 
                                "배송 정보가 업데이트되었습니다. 현재 배송중입니다.");
                    }
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("warning", 
                            "배송 정보는 저장되었지만 배송 추적 조회에 실패했습니다: " + e.getMessage());
                }
            } else {
                redirectAttributes.addFlashAttribute("success", "배송 정보가 업데이트되었습니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "배송 정보 업데이트 실패: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }
    
    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam Order.OrderStatus status,
                                    RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getOrder(id);
            order.setStatus(status);
            orderService.saveOrder(order);
            
            redirectAttributes.addFlashAttribute("success", "주문 상태가 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "주문 상태 변경 실패: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }
}

