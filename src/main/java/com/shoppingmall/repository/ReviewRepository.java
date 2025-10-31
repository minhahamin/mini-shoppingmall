package com.shoppingmall.repository;

import com.shoppingmall.entity.Product;
import com.shoppingmall.entity.Review;
import com.shoppingmall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // 상품별 리뷰 목록 (최신순)
    List<Review> findByProductOrderByCreatedAtDesc(Product product);
    
    // 사용자별 리뷰 목록
    List<Review> findByUserOrderByCreatedAtDesc(User user);
    
    // 주문 항목별 리뷰 조회 (중복 리뷰 방지)
    Optional<Review> findByOrderItemId(Long orderItemId);
    
    // 상품별 평균 평점 계산
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product")
    Double calculateAverageRating(@Param("product") Product product);
    
    // 상품별 리뷰 개수
    Long countByProduct(Product product);
    
    // 사용자가 특정 상품에 리뷰를 작성했는지 확인
    boolean existsByUserAndProduct(User user, Product product);
    
    // 사용자가 특정 주문 항목에 리뷰를 작성했는지 확인
    boolean existsByUserAndOrderItemId(User user, Long orderItemId);
}

