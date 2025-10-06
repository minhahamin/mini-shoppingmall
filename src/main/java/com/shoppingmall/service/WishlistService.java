package com.shoppingmall.service;

import com.shoppingmall.entity.Product;
import com.shoppingmall.entity.User;
import com.shoppingmall.entity.Wishlist;
import com.shoppingmall.repository.ProductRepository;
import com.shoppingmall.repository.UserRepository;
import com.shoppingmall.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {
    
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    // 찜 추가
    public void addToWishlist(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다"));
        
        // 이미 찜한 상품인지 확인
        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            throw new IllegalArgumentException("이미 찜한 상품입니다");
        }
        
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();
        
        wishlistRepository.save(wishlist);
    }
    
    // 찜 제거
    public void removeFromWishlist(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다"));
        
        wishlistRepository.deleteByUserAndProduct(user, product);
    }
    
    // 찜 목록 조회
    @Transactional(readOnly = true)
    public List<Wishlist> getUserWishlist(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        return wishlistRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    // 찜 여부 확인
    @Transactional(readOnly = true)
    public boolean isInWishlist(String username, Long productId) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            Product product = productRepository.findById(productId).orElse(null);
            
            if (user == null || product == null) {
                return false;
            }
            
            return wishlistRepository.existsByUserAndProduct(user, product);
        } catch (Exception e) {
            return false;
        }
    }
}

