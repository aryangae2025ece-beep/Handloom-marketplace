package com.handloom.marketplace.dto;

import com.handloom.marketplace.entity.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public class ProductForm {

    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Name is too long")
    private String name;

    @Size(max = 2000, message = "Description is too long (max 2000 characters)")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1.00", message = "Price must be at least 1")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @NotNull(message = "Please choose a category")
    private Long categoryId;

    @Size(max = 100, message = "Too long")
    private String craftType;

    @Size(max = 100, message = "Too long")
    private String region;

    private MultipartFile image;

    // only used to preview the current image when editing
    private String currentImageUrl;

    public static ProductForm from(Product p) {
        ProductForm f = new ProductForm();
        f.setId(p.getId());
        f.setName(p.getName());
        f.setDescription(p.getDescription());
        f.setPrice(p.getPrice());
        f.setStock(p.getStock());
        f.setCategoryId(p.getCategory().getId());
        f.setCraftType(p.getCraftType());
        f.setRegion(p.getRegion());
        f.setCurrentImageUrl(p.getImageUrl());
        return f;
    }

    // <accessors>

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCraftType() {
        return craftType;
    }

    public void setCraftType(String craftType) {
        this.craftType = craftType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getCurrentImageUrl() {
        return currentImageUrl;
    }

    public void setCurrentImageUrl(String currentImageUrl) {
        this.currentImageUrl = currentImageUrl;
    }
}
