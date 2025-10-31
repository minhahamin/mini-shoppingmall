package com.shoppingmall.repository;

import com.shoppingmall.entity.OrderItem;
import com.shoppingmall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // 사용자가 리뷰 작성 가능한 주문 항목 조회 (결제 완료된 주문의 항목 중 리뷰가 없는 것)
    @Query("SELECT oi FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.user = :user " +
           "AND o.status = com.shoppingmall.entity.Order$OrderStatus.PAID " +
           "AND NOT EXISTS (SELECT 1 FROM Review r WHERE r.orderItem = oi AND r.user = :user) " +
           "ORDER BY o.paidAt DESC")
    List<OrderItem> findReviewableItemsByUser(@Param("user") User user);
}

