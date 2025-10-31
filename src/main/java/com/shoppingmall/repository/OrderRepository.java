package com.shoppingmall.repository;

import com.shoppingmall.entity.Order;
import com.shoppingmall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findByUserAndStatusOrderByCreatedAtDesc(User user, Order.OrderStatus status);
    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByStripeSessionId(String sessionId);
    
    // 결제 완료된 주문만 조회
    List<Order> findByStatus(Order.OrderStatus status);
    
    // 기간별 매출 통계 (일별)
    @Query("SELECT CAST(o.paidAt AS DATE) as date, SUM(o.totalAmount) as total, COUNT(o) as count " +
           "FROM Order o " +
           "WHERE o.status = :status AND o.paidAt >= :startDate AND o.paidAt < :endDate " +
           "GROUP BY CAST(o.paidAt AS DATE) " +
           "ORDER BY date ASC")
    List<Object[]> findDailySales(@Param("status") Order.OrderStatus status,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);
    
    // 월별 매출 통계 (PostgreSQL native query)
    @Query(value = "SELECT TO_CHAR(o.paid_at, 'YYYY-MM') as month, " +
                    "COALESCE(SUM(o.total_amount), 0) as total, " +
                    "COUNT(o.id) as count " +
                    "FROM orders o " +
                    "WHERE o.status = :statusStr " +
                    "AND o.paid_at >= :startDate AND o.paid_at < :endDate " +
                    "GROUP BY TO_CHAR(o.paid_at, 'YYYY-MM') " +
                    "ORDER BY month ASC", nativeQuery = true)
    List<Object[]> findMonthlySales(@Param("statusStr") String statusStr,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
    
    // 전체 매출액
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal getTotalRevenue(@Param("status") Order.OrderStatus status);
    
    // 총 주문 수
    Long countByStatus(Order.OrderStatus status);
}

