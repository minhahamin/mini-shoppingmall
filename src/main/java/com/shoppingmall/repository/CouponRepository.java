package com.shoppingmall.repository;

import com.shoppingmall.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    Optional<Coupon> findByCode(String code);
    
    List<Coupon> findByActiveTrueOrderByCreatedAtDesc();
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true " +
           "AND c.validFrom <= :now AND c.validTo >= :now " +
           "AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit)")
    List<Coupon> findAvailableCoupons(LocalDateTime now);
}

