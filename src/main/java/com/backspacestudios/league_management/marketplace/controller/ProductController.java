package com.backspacestudios.league_management.marketplace.controller;

import com.backspacestudios.league_management.marketplace.dto.*;
import com.backspacestudios.league_management.marketplace.enums.ProductCategory;
import com.backspacestudios.league_management.marketplace.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/marketplace/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Store owner endpoints (require authentication + ownership)
    @PostMapping
    @PreAuthorize("isAuthenticated()") // further check inside service
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestPart("product") ProductRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request, images));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestPart("product") ProductRequest request,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) throws IOException {
        return ResponseEntity.ok(productService.updateProduct(productId, request, newImages));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProductImage(@PathVariable UUID imageId) {
        productService.deleteProductImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/stock")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateStock(@PathVariable UUID productId, @Valid @RequestBody UpdateStockRequest request) {
        productService.updateStock(productId, request);
        return ResponseEntity.ok().build();
    }

    // Public endpoints (anyone can view)
    @GetMapping("/available")
    public ResponseEntity<Page<ProductResponse>> listAvailableProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(productService.listAvailableProducts(pageable));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ProductResponse>> listByCategory(
            @PathVariable ProductCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productService.listProductsByCategory(category, pageable));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByStore(
            @PathVariable UUID storeId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByStore(storeId, pageable));
    }
}