package com.shoppingmall.repository;

import com.shoppingmall.entity.User;
import com.shoppingmall.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    
    List<UserCoupon> findByUserAndUsedFalseOrderByCreatedAtDesc(User user);
    
    Optional<UserCoupon> findByUserAndCouponAndUsedFalse(User user, com.shoppingmall.entity.Coupon coupon);
    
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.user = :user " +
           "AND uc.used = false AND uc.coupon.active = true " +
           "AND uc.coupon.validFrom <= CURRENT_TIMESTAMP " +
           "AND uc.coupon.validTo >= CURRENT_TIMESTAMP " +
           "AND (uc.coupon.usageLimit IS NULL OR uc.coupon.usedCount < uc.coupon.usageLimit) " +
           "ORDER BY uc.createdAt DESC")
    List<UserCoupon> findUsableCouponsByUser(User user);
    
    List<UserCoupon> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<UserCoupon> findByIdAndUser(Long id, User user);
}

