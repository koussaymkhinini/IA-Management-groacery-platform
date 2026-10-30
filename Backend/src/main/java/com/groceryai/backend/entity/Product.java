package com.groceryai.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(precision = 6, scale = 4)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "product_class", length = 50)
    private String productClass;

    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    @Column(length = 50)
    private String resistant;

    @Column(name = "is_allergic", length = 20)
    private String isAllergic;

    @Column(name = "vitality_days")
    private BigDecimal vitalityDays;

    protected Product() {
        // Required by JPA
    }

    public Product(
            Integer productId,
            String productName,
            BigDecimal price,
            Category category,
            String productClass,
            LocalDateTime modifyDate,
            String resistant,
            String isAllergic,
            BigDecimal vitalityDays) {

        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.category = category;
        this.productClass = productClass;
        this.modifyDate = modifyDate;
        this.resistant = resistant;
        this.isAllergic = isAllergic;
        this.vitalityDays = vitalityDays;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public String getProductClass() {
        return productClass;
    }

    public LocalDateTime getModifyDate() {
        return modifyDate;
    }

    public String getResistant() {
        return resistant;
    }

    public String getIsAllergic() {
        return isAllergic;
    }

    public BigDecimal getVitalityDays() {
        return vitalityDays;
    }

    public void update(
            String productName,
            BigDecimal price,
            Category category,
            String productClass,
            LocalDateTime modifyDate,
            String resistant,
            String isAllergic,
            BigDecimal vitalityDays) {

        this.productName = productName;
        this.price = price;
        this.category = category;
        this.productClass = productClass;
        this.modifyDate = modifyDate;
        this.resistant = resistant;
        this.isAllergic = isAllergic;
        this.vitalityDays = vitalityDays;
    }
}