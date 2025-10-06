package com.shoppingmall.service;

import com.shoppingmall.dto.ProductDto;
import com.shoppingmall.entity.Product;
import com.shoppingmall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public Product createProduct(ProductDto productDto) {
        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .stock(productDto.getStock())
                .imageUrl(productDto.getImageUrl())
                .category(productDto.getCategory() != null ? productDto.getCategory() : "기타")
                .available(productDto.getAvailable() != null ? productDto.getAvailable() : true)
                .build();
        
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다"));
        
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());
        product.setImageUrl(productDto.getImageUrl());
        product.setCategory(productDto.getCategory());
        product.setAvailable(productDto.getAvailable());
        
        return productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다"));
    }
    
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findByAvailableTrue();
    }
    
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    @Transactional(readOnly = true)
    public List<Product> getTopSellingProducts() {
        return productRepository.findTop3ByAvailableTrueOrderBySalesCountDesc();
    }
    
    @Transactional(readOnly = true)
    public List<Product> getLatestProducts(int limit) {
        List<Product> products = productRepository.findByAvailableTrueOrderByCreatedAtDesc();
        return products.size() > limit ? products.subList(0, limit) : products;
    }
    
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }
}

