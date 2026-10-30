package com.project.code.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class OrderItem {

    /** Primary key, auto-incremented by the database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The order this item belongs to. */
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonManagedReference
    private OrderDetails order;

    /** The product that was ordered. */
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonManagedReference
    private Product product;

    /** How many units of the product were ordered. */
    private Integer quantity;

    /** Price of the product at the time of the order. */
    private Double price;

    public OrderItem() {}

    public OrderItem(OrderDetails order, Product product, Integer quantity, Double price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrderDetails getOrder() { return order; }
    public void setOrder(OrderDetails order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}