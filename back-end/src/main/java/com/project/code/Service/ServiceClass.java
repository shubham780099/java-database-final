package com.project.code.Service;

import org.springframework.stereotype.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;

@Service
public class ServiceClass {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public ServiceClass(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    /** Returns false if an inventory record already exists for this product and store. */
    public boolean validateInventory(Inventory inventory) {
        Inventory existing = inventoryRepository.findByProductIdandStoreId(
                inventory.getProduct().getId(), inventory.getStore().getId());
        return existing == null;
    }

    /** Returns false if a product with the same name already exists. */
    public boolean validateProduct(Product product) {
        return productRepository.findByName(product.getName()) == null;
    }

    /** Returns false if no product exists with this ID. */
    public boolean ValidateProductId(long id) {
        return productRepository.findByid(id) != null;
    }

    /** Fetches the inventory record for the product and store of the given inventory. */
    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepository.findByProductIdandStoreId(
                inventory.getProduct().getId(), inventory.getStore().getId());
    }
}