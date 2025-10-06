package com.shoppingmall.controller;

import com.shoppingmall.dto.ProductDto;
import com.shoppingmall.entity.Product;
import com.shoppingmall.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    
    private final ProductService productService;
    
    @GetMapping("/products")
    public String productList(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String search,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> productPage;
        
        if (search != null && !search.isEmpty()) {
            productPage = productService.searchProducts(search, pageable);
            model.addAttribute("search", search);
        } else {
            productPage = productService.getAllProducts(pageable);
        }
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("pageSize", size);
        
        return "admin/product-list";
    }
    
    @GetMapping("/products/new")
    public String createProductForm(Model model) {
        model.addAttribute("product", new ProductDto());
        model.addAttribute("isEdit", false);
        return "admin/product-form";
    }
    
    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute("product") ProductDto productDto,
                               BindingResult result,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/product-form";
        }
        
        try {
            productService.createProduct(productDto);
            redirectAttributes.addFlashAttribute("success", "상품이 등록되었습니다");
            return "redirect:/admin/products?page=" + page + "&size=" + size;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 등록에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/products/new?page=" + page + "&size=" + size;
        }
    }
    
    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        Product product = productService.getProduct(id);
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setStock(product.getStock());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setCategory(product.getCategory());
        productDto.setAvailable(product.getAvailable());
        
        model.addAttribute("product", productDto);
        model.addAttribute("isEdit", true);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "admin/product-form";
    }
    
    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id,
                               @Valid @ModelAttribute("product") ProductDto productDto,
                               BindingResult result,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/product-form";
        }
        
        try {
            productService.updateProduct(id, productDto);
            redirectAttributes.addFlashAttribute("success", "상품이 수정되었습니다");
            return "redirect:/admin/products?page=" + page + "&size=" + size;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/products/" + id + "/edit?page=" + page + "&size=" + size;
        }
    }
    
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "상품이 삭제되었습니다");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 삭제에 실패했습니다: " + e.getMessage());
        }
        return "redirect:/admin/products?page=" + page + "&size=" + size;
    }
}

