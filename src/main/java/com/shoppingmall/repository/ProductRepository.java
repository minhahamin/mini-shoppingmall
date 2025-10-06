package com.shoppingmall.repository;

import com.shoppingmall.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByAvailableTrue();
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    
    // 판매량 순으로 조회
    List<Product> findTop3ByAvailableTrueOrderBySalesCountDesc();
    List<Product> findByAvailableTrueOrderByCreatedAtDesc();
}

