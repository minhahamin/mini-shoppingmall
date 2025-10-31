package com.shoppingmall.service;

import com.shoppingmall.entity.Coupon;
import com.shoppingmall.entity.User;
import com.shoppingmall.entity.UserCoupon;
import com.shoppingmall.repository.CouponRepository;
import com.shoppingmall.repository.UserCouponRepository;
import com.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {
    
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    
    // 쿠폰 생성
    public Coupon createCoupon(Coupon coupon) {
        // 쿠폰 코드 중복 확인
        if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 쿠폰 코드입니다");
        }
        return couponRepository.save(coupon);
    }
    
    // 모든 쿠폰 조회 (관리자용)
    @Transactional(readOnly = true)
    public List<Coupon> getAllCoupons() {
        return couponRepository.findByActiveTrueOrderByCreatedAtDesc();
    }
    
    // 쿠폰 조회
    @Transactional(readOnly = true)
    public Coupon getCoupon(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다"));
    }
    
    // 쿠폰 코드로 조회
    @Transactional(readOnly = true)
    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다"));
    }
    
    // 쿠폰 수정
    public Coupon updateCoupon(Long id, Coupon coupon) {
        Coupon existingCoupon = getCoupon(id);
        
        // 코드 변경 시 중복 확인
        if (!existingCoupon.getCode().equals(coupon.getCode())) {
            if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 쿠폰 코드입니다");
            }
        }
        
        existingCoupon.setCode(coupon.getCode());
        existingCoupon.setName(coupon.getName());
        existingCoupon.setDescription(coupon.getDescription());
        existingCoupon.setDiscountType(coupon.getDiscountType());
        existingCoupon.setDiscountValue(coupon.getDiscountValue());
        existingCoupon.setMinPurchaseAmount(coupon.getMinPurchaseAmount());
        existingCoupon.setUsageLimit(coupon.getUsageLimit());
        existingCoupon.setValidFrom(coupon.getValidFrom());
        existingCoupon.setValidTo(coupon.getValidTo());
        existingCoupon.setActive(coupon.getActive());
        
        return couponRepository.save(existingCoupon);
    }
    
    // 쿠폰 삭제 (비활성화)
    public void deleteCoupon(Long id) {
        Coupon coupon = getCoupon(id);
        coupon.setActive(false);
        couponRepository.save(coupon);
    }
    
    // 회원에게 쿠폰 지급 (특정 쿠폰을 특정 회원에게 지급)
    public UserCoupon issueCouponToUser(Long couponId, String username) {
        Coupon coupon = getCoupon(couponId);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        // 이미 보유한 쿠폰인지 확인
        if (userCouponRepository.findByUserAndCouponAndUsedFalse(user, coupon).isPresent()) {
            throw new IllegalArgumentException("이미 보유한 쿠폰입니다");
        }
        
        UserCoupon userCoupon = UserCoupon.builder()
                .user(user)
                .coupon(coupon)
                .used(false)
                .build();
        
        return userCouponRepository.save(userCoupon);
    }
    
    // 모든 회원에게 쿠폰 일괄 지급
    public void issueCouponToAllUsers(Long couponId) {
        Coupon coupon = getCoupon(couponId);
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            // 이미 보유한 쿠폰이 아닌 경우에만 지급
            if (userCouponRepository.findByUserAndCouponAndUsedFalse(user, coupon).isEmpty()) {
                UserCoupon userCoupon = UserCoupon.builder()
                        .user(user)
                        .coupon(coupon)
                        .used(false)
                        .build();
                userCouponRepository.save(userCoupon);
            }
        }
    }
    
    // 특정 사용자들에게 쿠폰 지급
    public void issueCouponToUsers(Long couponId, List<String> usernames) {
        Coupon coupon = getCoupon(couponId);
        
        for (String username : usernames) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
            
            // 이미 보유한 쿠폰이 아닌 경우에만 지급
            if (userCouponRepository.findByUserAndCouponAndUsedFalse(user, coupon).isEmpty()) {
                UserCoupon userCoupon = UserCoupon.builder()
                        .user(user)
                        .coupon(coupon)
                        .used(false)
                        .build();
                userCouponRepository.save(userCoupon);
            }
        }
    }
    
    // 회원의 사용 가능한 쿠폰 목록 조회
    @Transactional(readOnly = true)
    public List<UserCoupon> getUsableCouponsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        return userCouponRepository.findUsableCouponsByUser(user);
    }
    
    // 회원의 모든 쿠폰 목록 조회
    @Transactional(readOnly = true)
    public List<UserCoupon> getAllCouponsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        return userCouponRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    // 쿠폰 사용 처리
    public void useCoupon(Long userCouponId, Long orderId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        UserCoupon userCoupon = userCouponRepository.findByIdAndUser(userCouponId, user)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다"));
        
        if (userCoupon.getUsed()) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다");
        }
        
        if (!userCoupon.isUsable()) {
            throw new IllegalArgumentException("사용할 수 없는 쿠폰입니다");
        }
        
        userCoupon.markAsUsed(orderId);
        userCouponRepository.save(userCoupon);
        
        // 쿠폰 사용 횟수 업데이트
        Coupon coupon = userCoupon.getCoupon();
        couponRepository.save(coupon);
    }
    
    // 할인 금액 계산
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(Long userCouponId, BigDecimal orderAmount, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        UserCoupon userCoupon = userCouponRepository.findByIdAndUser(userCouponId, user)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다"));
        
        if (userCoupon.getUsed()) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다");
        }
        
        Coupon coupon = userCoupon.getCoupon();
        return coupon.calculateDiscount(orderAmount);
    }
}

