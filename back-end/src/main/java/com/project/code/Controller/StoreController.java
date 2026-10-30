package com.project.code.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderService orderService;

    /** Creates a new store and returns its ID in the message. */
    @PostMapping
    public ResponseEntity<Map<String, String>> addStore(@RequestBody Store store) {
        Map<String, String> map = new HashMap<>();
        try {
            Store saved = storeRepository.save(store);
            map.put("message", "Store created successfully with id: " + saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(map); // 201
        } catch (Exception e) {
            map.put("message", "Error creating store: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map); // 400
        }
    }

    /** Returns true (200) if the store exists, or false (404) if it does not. */
    @GetMapping("validate/{storeId}")
    public ResponseEntity<Boolean> validateStore(@PathVariable("storeId") Long storeId) {
        boolean exists = storeRepository.findByid(storeId) != null;
        if (exists) {
            return ResponseEntity.ok(true); // 200
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // 404
    }

    /** Places an order. Success returns "message"; failure returns the error text. */
    @PostMapping("/placeOrder")
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.saveOrder(placeOrderRequest);
            response.put("message", "Order placed successfully");
            return ResponseEntity.ok(response); // 200
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            response.put("error", e.getMessage()); // the pre-built frontend reads the lowercase key
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400
        }
    }
}