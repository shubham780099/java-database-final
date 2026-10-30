package com.project.code.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;

    /** Adds a new product. Rejects duplicate names and duplicate SKUs. */
    @PostMapping
    public ResponseEntity<Map<String, String>> addProduct(@RequestBody Product product) {
        Map<String, String> map = new HashMap<>();

        if (!serviceClass.validateProduct(product)) {
            map.put("message", "Product already present in database");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map); // 409
        }
        try {
            productRepository.save(product);
            map.put("message", "Product added successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(map); // 201
        } catch (DataIntegrityViolationException e) {
            map.put("message", "SKU should be unique");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(map); // 409
        } catch (Exception e) {
            map.put("message", "Error adding product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map); // 500
        }
    }

    /** Retrieves one product by ID (full path: /product/product/{id}, as the frontend expects). */
    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> getProductbyId(@PathVariable("id") Long id) {
        Map<String, Object> map = new HashMap<>();
        try {
            Product product = productRepository.findByid(id);
            if (product == null) {
                map.put("message", "Product with id " + id + " not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map); // 404
            }
            map.put("products", product);
            return ResponseEntity.ok(map); // 200
        } catch (Exception e) {
            map.put("message", "Error retrieving product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map); // 500
        }
    }

    /** Updates an existing product. */
    @PutMapping
    public ResponseEntity<Map<String, String>> updateProduct(@RequestBody Product product) {
        Map<String, String> map = new HashMap<>();
        try {
            productRepository.save(product);
            map.put("message", "Data updated successfully");
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            map.put("message", "Error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
        }
    }

    /** Filters by name and/or category. The text "null" means "don't filter by this value". */
    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(@PathVariable("name") String name,
                                                       @PathVariable("category") String category) {
        Map<String, Object> map = new HashMap<>();
        if (name.equals("null")) {
            map.put("products", productRepository.findByCategory(category));
        } else if (category.equals("null")) {
            map.put("products", productRepository.findProductBySubName(name));
        } else {
            map.put("products", productRepository.findProductBySubNameAndCategory(name, category));
        }
        return map;
    }

    /** Lists all products. */
    @GetMapping
    public Map<String, Object> listProduct() {
        Map<String, Object> map = new HashMap<>();
        map.put("products", productRepository.findAll());
        return map;
    }

    /** Filters a store's products by category. */
    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(@PathVariable("category") String category,
                                                              @PathVariable("storeid") Long storeid) {
        Map<String, Object> map = new HashMap<>();
        map.put("product", productRepository.findProductByCategory(category, storeid));
        return map;
    }

    /**
     * Deletes a product along with its inventory and order items.
     * Runs in one transaction, so if any step fails nothing is deleted.
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable("id") Long id) {
        Map<String, String> map = new HashMap<>();

        if (!serviceClass.ValidateProductId(id)) {
            map.put("message", "Id " + id + " not present in database");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map); // 404
        }
        try {
            // Delete child rows first, because they reference the product by foreign key
            inventoryRepository.deleteByProductId(id);
            orderItemRepository.deleteByProductId(id);
            productRepository.deleteById(id);

            map.put("message", "Deleted product successfully with id: " + id);
            return ResponseEntity.ok(map); // 200
        } catch (Exception e) {
            map.put("message", "Error deleting product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map); // 500
        }
    }

    /** Searches products by name text. */
    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable("name") String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("products", productRepository.findProductBySubName(name));
        return map;
    }
}