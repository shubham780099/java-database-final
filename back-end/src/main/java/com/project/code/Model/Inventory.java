package com.project.code.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Inventory {

    /** Primary key, auto-incremented by the database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The product this stock entry is for. */
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference("inventory-product")
    private Product product;

    /** The store that holds this stock. */
    @ManyToOne
    @JoinColumn(name = "store_id")
    @JsonBackReference("inventory-store")
    private Store store;

    /** Current number of units in stock. */
    private Integer stockLevel;

    public Inventory() {}

    public Inventory(Product product, Store store, Integer stockLevel) {
        this.product = product;
        this.store = store;
        this.stockLevel = stockLevel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }

    public Integer getStockLevel() { return stockLevel; }
    public void setStockLevel(Integer stockLevel) { this.stockLevel = stockLevel; }

    @Override
    public String toString() {
        return "Inventory{id=" + id
                + ", product=" + (product != null ? product.getId() : "null")
                + ", store=" + (store != null ? store.getId() : "null")
                + ", stockLevel=" + stockLevel + "}";
    }
}