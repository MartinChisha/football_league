package com.backspacestudios.league_management.marketplace.service;

import com.backspacestudios.league_management.core.entity.User;
import com.backspacestudios.league_management.core.repository.UserRepository;
import com.backspacestudios.league_management.marketplace.dto.*;
import com.backspacestudios.league_management.marketplace.entity.*;
import com.backspacestudios.league_management.marketplace.enums.ProductCategory;
import com.backspacestudios.league_management.marketplace.repository.*;
import com.backspacestudios.league_management.core.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final BulkPricingTierRepository bulkPricingTierRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    // Helper to get storeId of current user
    private UUID getCurrentStoreId(UUID userId) {
        Store store = storeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No store found for user"));
        return store.getStoreId();
    }

    // Helper to get current user ID from security context (you may already have a utility)
    private UUID getCurrentUserId() {
        // Implementation depends on your security context. Example:
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getUserId();
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request, List<MultipartFile> images) throws IOException {
        UUID userId = getCurrentUserId();
        UUID storeId = getCurrentStoreId(userId);

        Product product = new Product();
        product.setStoreId(storeId);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        if (request.getTags() != null) product.setTags(request.getTags());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        product.setLowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 5);
        product.setWeightKg(request.getWeightKg());
        product.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);
        product.setMetadata(request.getMetadata());

        Product saved = productRepository.save(product);

        // Upload and save images
        if (images != null && !images.isEmpty()) {
            int order = 0;
            for (MultipartFile file : images) {
                String imageUrl = fileUploadService.saveProductImage(file, saved.getProductId(), order);
                ProductImage img = new ProductImage();
                img.setProduct(saved);
                img.setImageUrl(imageUrl);
                img.setDisplayOrder(order++);
                productImageRepository.save(img);
            }
        }

        // Save bulk pricing tiers
        if (request.getBulkPricingTiers() != null) {
            for (BulkPricingTierDTO tierDto : request.getBulkPricingTiers()) {
                BulkPricingTier tier = new BulkPricingTier();
                tier.setProduct(saved);
                tier.setMinQuantity(tierDto.getMinQuantity());
                tier.setMaxQuantity(tierDto.getMaxQuantity());
                tier.setDiscountPercentage(tierDto.getDiscountPercentage());
                tier.setFixedPrice(tierDto.getFixedPrice());
                bulkPricingTierRepository.save(tier);
            }
        }

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByStore(UUID storeId, Pageable pageable) {
        return productRepository.findByStoreId(storeId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, ProductRequest request, List<MultipartFile> newImages) throws IOException {
        UUID userId = getCurrentUserId();
        UUID storeId = getCurrentStoreId(userId);

        Product product = productRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new AccessDeniedException("You do not own this product"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        if (request.getTags() != null) product.setTags(request.getTags());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : product.getStockQuantity());
        product.setLowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : product.getLowStockThreshold());
        product.setWeightKg(request.getWeightKg());
        product.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : product.getIsAvailable());
        product.setMetadata(request.getMetadata());

        // Bulk pricing tiers: replace all
        bulkPricingTierRepository.deleteByProductProductId(productId);
        if (request.getBulkPricingTiers() != null) {
            for (BulkPricingTierDTO tierDto : request.getBulkPricingTiers()) {
                BulkPricingTier tier = new BulkPricingTier();
                tier.setProduct(product);
                tier.setMinQuantity(tierDto.getMinQuantity());
                tier.setMaxQuantity(tierDto.getMaxQuantity());
                tier.setDiscountPercentage(tierDto.getDiscountPercentage());
                tier.setFixedPrice(tierDto.getFixedPrice());
                bulkPricingTierRepository.save(tier);
            }
        }

        // Add new images (optional: also allow deletion of existing images)
        if (newImages != null && !newImages.isEmpty()) {
            int currentMaxOrder = product.getImages().stream()
                    .mapToInt(ProductImage::getDisplayOrder)
                    .max().orElse(-1);
            int order = currentMaxOrder + 1;
            for (MultipartFile file : newImages) {
                String imageUrl = fileUploadService.saveProductImage(file, productId, order);
                ProductImage img = new ProductImage();
                img.setProduct(product);
                img.setImageUrl(imageUrl);
                img.setDisplayOrder(order++);
                productImageRepository.save(img);
            }
        }

        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        UUID userId = getCurrentUserId();
        UUID storeId = getCurrentStoreId(userId);
        Product product = productRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new AccessDeniedException("You do not own this product"));
        // Images and tiers will be cascade deleted if JPA relationships are set.
        productRepository.delete(product);
    }

    @Transactional
    public void deleteProductImage(UUID imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        // Verify ownership via product
        UUID userId = getCurrentUserId();
        UUID storeId = getCurrentStoreId(userId);
        if (!image.getProduct().getStoreId().equals(storeId)) {
            throw new AccessDeniedException("Not your product");
        }
        productImageRepository.delete(image);
    }

    @Transactional
    public void updateStock(UUID productId, UpdateStockRequest request) {
        UUID userId = getCurrentUserId();
        UUID storeId = getCurrentStoreId(userId);
        Product product = productRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new AccessDeniedException("You do not own this product"));
        int newQuantity = product.getStockQuantity() + request.getDelta();
        if (newQuantity < 0) throw new IllegalArgumentException("Stock cannot be negative");
        product.setStockQuantity(newQuantity);
        productRepository.save(product);
    }

    // Public listing methods (no ownership required)
    @Transactional(readOnly = true)
    public Page<ProductResponse> listAvailableProducts(Pageable pageable) {
        return productRepository.findByIsAvailableTrue(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> listProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable)
                .map(this::mapToResponse);
    }

    private ProductResponse mapToResponse(Product product) {
        List<ProductImageResponse> imageResponses = product.getImages().stream()
                .sorted(Comparator.comparingInt(ProductImage::getDisplayOrder))
                .map(img -> ProductImageResponse.builder()
                        .imageId(img.getImageId())
                        .imageUrl(img.getImageUrl())
                        .displayOrder(img.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());

        List<BulkPricingTierDTO> tierDTOs = product.getBulkPricingTiers().stream()
                .sorted(Comparator.comparingInt(BulkPricingTier::getMinQuantity))
                .map(tier -> {
                    BulkPricingTierDTO dto = new BulkPricingTierDTO();
                    dto.setMinQuantity(tier.getMinQuantity());
                    dto.setMaxQuantity(tier.getMaxQuantity());
                    dto.setDiscountPercentage(tier.getDiscountPercentage());
                    dto.setFixedPrice(tier.getFixedPrice());
                    return dto;
                })
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .productId(product.getProductId())
                .storeId(product.getStoreId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .tags(product.getTags())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .lowStockThreshold(product.getLowStockThreshold())
                .weightKg(product.getWeightKg())
                .isAvailable(product.getIsAvailable())
                .metadata(product.getMetadata())
                .images(imageResponses)
                .bulkPricingTiers(tierDTOs)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}