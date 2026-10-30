package com.groceryai.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @Column(name = "sales_id")
    private Long salesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesperson_id", nullable = false)
    private Employee salesperson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 3, scale = 2)
    private BigDecimal discount;

    @Column(name = "total_price", precision = 14, scale = 4)
    private BigDecimal totalPrice;

    @Column(name = "sales_date")
    private LocalDateTime salesDate;

    @Column(name = "transaction_number", length = 50)
    private String transactionNumber;

    protected Sale() {}

    public Long getSalesId() {
        return salesId;
    }

    public Employee getSalesperson() {
        return salesperson;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getSalesDate() {
        return salesDate;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }
}