package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;

    /** Updates a product and its inventory entry. */
    @PutMapping
    public Map<String, String> updateInventory(@RequestBody CombinedRequest request) {
        Map<String, String> map = new HashMap<>();
        Product product = request.getProduct();
        Inventory inventory = request.getInventory();

        // Validate that the product exists
        if (product == null || product.getId() == null
                || !serviceClass.ValidateProductId(product.getId())) {
            map.put("message", "Product id not present in database");
            return map;
        }

        try {
            productRepository.save(product);
            map.put("message", "Successfully updated product with id: " + product.getId());

            if (inventory != null) {
                Inventory existing = serviceClass.getInventoryId(inventory);
                if (existing == null) {
                    map.put("message", "No data available for this product or store id");
                    return map;
                }
                // Reuse the existing row's id so save() updates instead of inserting
                inventory.setId(existing.getId());
                inventoryRepository.save(inventory);
            }
        } catch (DataIntegrityViolationException e) {
            map.put("message", "Error: data integrity violation - " + e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            map.put("message", "Error: " + e.getMessage());
        }
        return map;
    }

    /** Adds a new inventory entry, unless one already exists for this product and store. */
    @PostMapping
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> map = new HashMap<>();
        try {
            if (!serviceClass.validateInventory(inventory)) {
                map.put("message", "Data already present in inventory");
                return map;
            }
            inventoryRepository.save(inventory);
            map.put("message", "Product added to inventory successfully");
        } catch (DataIntegrityViolationException e) {
            map.put("message", "Error: data integrity violation - " + e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            map.put("message", "Error: " + e.getMessage());
        }
        return map;
    }

    /** Returns all products stocked in a store. */
    @GetMapping("/{storeid}")
    public Map<String, Object> getAllProducts(@PathVariable("storeid") Long storeid) {
        Map<String, Object> map = new HashMap<>();
        map.put("products", productRepository.findProductsByStoreId(storeid));
        return map;
    }

    /**
     * Filters a store's products by category and/or name.
     * The literal text "null" in the URL means "don't filter by this value".
     */
    @GetMapping("filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(@PathVariable("category") String category,
                                              @PathVariable("name") String name,
                                              @PathVariable("storeid") Long storeid) {
        Map<String, Object> map = new HashMap<>();
        boolean noCategory = category == null || category.isBlank() || category.equals("null");
        boolean noName = name == null || name.isBlank() || name.equals("null");

        List<Product> result;
        if (noCategory && noName) {
            // No filters given: return everything in the store
            result = productRepository.findProductsByStoreId(storeid);
        } else if (noCategory) {
            result = productRepository.findByNameLike(storeid, name);
        } else if (noName) {
            result = productRepository.findByCategoryAndStoreId(storeid, category);
        } else {
            result = productRepository.findByNameAndCategory(storeid, name, category);
        }
        map.put("product", result);
        return map;
    }

    /** Searches a store's products by name text. */
    @GetMapping("search/{name}/{storeId}")
    public Map<String, Object> searchProduct(@PathVariable("name") String name,
                                             @PathVariable("storeId") Long storeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("product", productRepository.findByNameLike(storeId, name));
        return map;
    }

    /** Deletes a product's inventory entries. */
    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable("id") Long id) {
        Map<String, String> map = new HashMap<>();
        if (!serviceClass.ValidateProductId(id)) {
            map.put("message", "Id " + id + " not present in database");
            return map;
        }
        inventoryRepository.deleteByProductId(id);
        map.put("message", "Deleted product successfully with id: " + id);
        return map;
    }

    /** True if the store has at least the requested quantity of the product in stock. */
    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable("quantity") int quantity,
                                    @PathVariable("storeId") Long storeId,
                                    @PathVariable("productId") Long productId) {
        Inventory result = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        if (result == null || result.getStockLevel() == null) {
            return false; // no inventory entry means nothing in stock
        }
        return result.getStockLevel() >= quantity;
    }
}