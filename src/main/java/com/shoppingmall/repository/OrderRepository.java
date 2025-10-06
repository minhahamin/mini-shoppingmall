package com.shoppingmall.repository;

import com.shoppingmall.entity.Order;
import com.shoppingmall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findByUserAndStatusOrderByCreatedAtDesc(User user, Order.OrderStatus status);
    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByStripeSessionId(String sessionId);
}

