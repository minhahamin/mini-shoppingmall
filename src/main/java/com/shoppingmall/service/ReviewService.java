package com.shoppingmall.service;

import com.shoppingmall.entity.Order;
import com.shoppingmall.entity.*;
import com.shoppingmall.repository.OrderItemRepository;
import com.shoppingmall.repository.ProductRepository;
import com.shoppingmall.repository.ReviewRepository;
import com.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    
    /**
     * 리뷰 작성
     */
    public Review createReview(String username, Long productId, Long orderItemId, Integer rating, String content) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다"));
        
        // 주문 항목이 있으면 해당 주문 항목 확인, 없으면 사용자가 주문한 상품인지 확인
        OrderItem orderItem = null;
        if (orderItemId != null) {
            orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new IllegalArgumentException("주문 항목을 찾을 수 없습니다"));
            
            // 주문 항목이 사용자의 것이고 결제 완료된 주문인지 확인
            if (!orderItem.getOrder().getUser().getUsername().equals(username)) {
                throw new IllegalArgumentException("본인의 주문 항목이 아닙니다");
            }
            if (orderItem.getOrder().getStatus() != Order.OrderStatus.PAID) {
                throw new IllegalArgumentException("결제 완료된 주문에 대해서만 리뷰를 작성할 수 있습니다");
            }
            
            // 이미 리뷰를 작성했는지 확인
            if (reviewRepository.existsByUserAndOrderItemId(user, orderItemId)) {
                throw new IllegalArgumentException("이미 리뷰를 작성하셨습니다");
            }
        } else {
            // orderItemId가 없으면 사용자가 해당 상품을 주문했는지 확인
            if (!hasOrderedProduct(username, productId)) {
                throw new IllegalArgumentException("주문한 상품에 대해서만 리뷰를 작성할 수 있습니다");
            }
        }
        
        Review review = Review.builder()
                .user(user)
                .product(product)
                .orderItem(orderItem)
                .rating(rating)
                .content(content)
                .build();
        
        Review savedReview = reviewRepository.save(review);
        
        // 상품 평점 업데이트
        updateProductRating(product);
        
        return savedReview;
    }
    
    /**
     * 리뷰 수정
     */
    public Review updateReview(Long reviewId, String username, Integer rating, String content) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다"));
        
        // 본인의 리뷰인지 확인
        if (!review.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("본인의 리뷰만 수정할 수 있습니다");
        }
        
        review.setRating(rating);
        review.setContent(content);
        
        Review savedReview = reviewRepository.save(review);
        
        // 상품 평점 업데이트
        updateProductRating(review.getProduct());
        
        return savedReview;
    }
    
    /**
     * 리뷰 삭제
     */
    public void deleteReview(Long reviewId, String username) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다"));
        
        // 본인의 리뷰인지 확인
        if (!review.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("본인의 리뷰만 삭제할 수 있습니다");
        }
        
        Product product = review.getProduct();
        reviewRepository.delete(review);
        
        // 상품 평점 업데이트
        updateProductRating(product);
    }
    
    /**
     * 상품의 평점 및 리뷰 개수 업데이트
     */
    private void updateProductRating(Product product) {
        Double avgRating = reviewRepository.calculateAverageRating(product);
        Long reviewCount = reviewRepository.countByProduct(product);
        
        if (avgRating != null) {
            product.setAverageRating(BigDecimal.valueOf(avgRating).setScale(2, java.math.RoundingMode.HALF_UP));
        } else {
            product.setAverageRating(BigDecimal.ZERO);
        }
        
        product.setReviewCount(reviewCount.intValue());
        productRepository.save(product);
    }
    
    /**
     * 상품별 리뷰 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Review> getReviewsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다"));
        return reviewRepository.findByProductOrderByCreatedAtDesc(product);
    }
    
    /**
     * 사용자별 리뷰 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Review> getReviewsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        return reviewRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    /**
     * 리뷰 조회
     */
    @Transactional(readOnly = true)
    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다"));
    }
    
    /**
     * 사용자가 특정 주문 항목에 리뷰를 작성할 수 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean canWriteReview(String username, Long orderItemId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        return !reviewRepository.existsByUserAndOrderItemId(user, orderItemId);
    }
    
    /**
     * 사용자가 주문한 상품 목록 조회 (리뷰 작성 가능한 상품)
     */
    @Transactional(readOnly = true)
    public List<OrderItem> getReviewableOrderItems(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        // 결제 완료된 주문의 항목 중 리뷰가 없는 것들
        return orderItemRepository.findReviewableItemsByUser(user);
    }
    
    /**
     * 사용자가 특정 상품을 주문했는지 확인 (결제 완료된 주문만)
     */
    @Transactional(readOnly = true)
    public boolean hasOrderedProduct(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        // 사용자가 결제 완료된 주문에서 해당 상품을 주문했는지 확인
        return orderItemRepository.findReviewableItemsByUser(user).stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
    }
    
    /**
     * 사용자가 특정 상품에 대해 리뷰 작성 가능한 주문 항목 조회
     */
    @Transactional(readOnly = true)
    public List<OrderItem> getReviewableOrderItemsForProduct(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        return orderItemRepository.findReviewableItemsByUser(user).stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .collect(java.util.stream.Collectors.toList());
    }
}

