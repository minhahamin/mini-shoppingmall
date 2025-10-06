package com.shoppingmall.controller;

import com.shoppingmall.dto.ProductDto;
import com.shoppingmall.entity.Product;
import com.shoppingmall.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public String productList(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
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
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/product-form";
        }
        
        try {
            productService.createProduct(productDto);
            redirectAttributes.addFlashAttribute("success", "상품이 등록되었습니다");
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 등록에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/products/new";
        }
    }
    
    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
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
        return "admin/product-form";
    }
    
    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id,
                               @Valid @ModelAttribute("product") ProductDto productDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/product-form";
        }
        
        try {
            productService.updateProduct(id, productDto);
            redirectAttributes.addFlashAttribute("success", "상품이 수정되었습니다");
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 수정에 실패했습니다: " + e.getMessage());
            return "redirect:/admin/products/" + id + "/edit";
        }
    }
    
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "상품이 삭제되었습니다");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상품 삭제에 실패했습니다: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
}

