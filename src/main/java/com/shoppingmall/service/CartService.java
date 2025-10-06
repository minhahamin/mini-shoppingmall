package com.shoppingmall.service;

import com.shoppingmall.entity.Cart;
import com.shoppingmall.entity.CartItem;
import com.shoppingmall.entity.Product;
import com.shoppingmall.entity.User;
import com.shoppingmall.repository.CartItemRepository;
import com.shoppingmall.repository.CartRepository;
import com.shoppingmall.repository.ProductRepository;
import com.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    // 사용자의 장바구니 가져오기 (없으면 생성)
    public Cart getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(cart);
                });
    }
    
    // 장바구니에 상품 추가
    public void addToCart(String username, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(username);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다"));
        
        // 재고 확인
        if (!product.getAvailable()) {
            throw new IllegalArgumentException("판매 중지된 상품입니다");
        }
        
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다");
        }
        
        // 이미 장바구니에 있는지 확인
        CartItem existingItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElse(null);
        
        if (existingItem != null) {
            // 이미 있으면 수량 증가
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new IllegalArgumentException("재고가 부족합니다");
            }
            existingItem.setQuantity(newQuantity);
        } else {
            // 새로운 항목 추가
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();
            cart.addItem(cartItem);
            cartItemRepository.save(cartItem);
        }
    }
    
    // 장바구니 항목 수량 변경
    public void updateQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다"));
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다");
        }
        
        if (cartItem.getProduct().getStock() < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다");
        }
        
        cartItem.setQuantity(quantity);
    }
    
    // 장바구니 항목 삭제
    public void removeFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다"));
        
        cartItemRepository.delete(cartItem);
    }
    
    // 장바구니 비우기
    public void clearCart(String username) {
        Cart cart = getOrCreateCart(username);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
    
    // CartItem ID들로 장바구니에서 제거
    public void removeCartItems(List<Long> cartItemIds) {
        System.out.println("===== 장바구니 항목 삭제 =====");
        System.out.println("삭제할 CartItem ID들: " + cartItemIds);
        
        for (Long cartItemId : cartItemIds) {
            try {
                CartItem item = cartItemRepository.findById(cartItemId).orElse(null);
                if (item != null) {
                    System.out.println("삭제: " + item.getProduct().getName() + " (CartItem ID: " + cartItemId + ")");
                    cartItemRepository.deleteById(cartItemId);
                }
            } catch (Exception e) {
                System.out.println("삭제 실패 (CartItem ID: " + cartItemId + "): " + e.getMessage());
            }
        }
        
        System.out.println("===== 삭제 완료 =====");
    }
    
    // 장바구니 총액 계산
    public BigDecimal getCartTotal(Cart cart) {
        return cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // 장바구니 항목 수 계산
    @Transactional(readOnly = true)
    public int getCartItemCount(String username) {
        try {
            Cart cart = getOrCreateCart(username);
            return cart.getItems().stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();
        } catch (Exception e) {
            return 0;
        }
    }
}

