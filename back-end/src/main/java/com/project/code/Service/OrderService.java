package com.project.code.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.code.Model.Customer;
import com.project.code.Model.Inventory;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.OrderItem;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.PurchaseProductDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderDetailsRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Repo.StoreRepository;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    /**
     * Saves an order: finds or creates the customer, saves the order details,
     * reduces stock for each product, and saves each order item.
     * If anything fails, the whole order is rolled back.
     */
    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {

        // 1. Retrieve the customer by email, or create a new one
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        if (customer == null) {
            customer = new Customer();
            customer.setName(placeOrderRequest.getCustomerName());
            customer.setEmail(placeOrderRequest.getCustomerEmail());
            customer.setPhone(placeOrderRequest.getCustomerPhone());
            customer = customerRepository.save(customer);
        }

        // 2. Retrieve the store, or fail if it doesn't exist
        Store store = storeRepository.findById(placeOrderRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // 3. Create and save the order details (use the saved result, it has the generated ID)
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setTotalPrice(placeOrderRequest.getTotalPrice());
        orderDetails.setDate(LocalDateTime.now());
        orderDetails = orderDetailsRepository.save(orderDetails);

        // 4. For each purchased product: reduce stock, then save the order item
        List<PurchaseProductDTO> purchaseProducts = placeOrderRequest.getPurchaseProduct();
        for (PurchaseProductDTO productDTO : purchaseProducts) {

            Inventory inventory = inventoryRepository.findByProductIdandStoreId(
                    productDTO.getId(), placeOrderRequest.getStoreId());
            if (inventory == null) {
                throw new RuntimeException("Product " + productDTO.getId() + " not found in this store");
            }
            if (inventory.getStockLevel() < productDTO.getQuantity()) {
                throw new RuntimeException("Not enough stock for product " + productDTO.getId());
            }

            inventory.setStockLevel(inventory.getStockLevel() - productDTO.getQuantity());
            inventoryRepository.save(inventory);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(orderDetails);
            orderItem.setProduct(productRepository.findByid(productDTO.getId()));
            orderItem.setQuantity(productDTO.getQuantity());
            orderItem.setPrice(productDTO.getPrice());
            orderItemRepository.save(orderItem);
        }
    }
}