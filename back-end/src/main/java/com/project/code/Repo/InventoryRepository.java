package com.project.code.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Inventory;

import jakarta.transaction.Transactional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /** Find the inventory record for one product in one store. */
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.store.id = :storeId")
    Inventory findByProductIdandStoreId(@Param("productId") Long productId,
                                        @Param("storeId") Long storeId);

    /** Find all inventory records for a given store. */
    List<Inventory> findByStore_Id(Long storeId);

    /** Delete all inventory records for a given product. */
    @Modifying
    @Transactional
    @Query("DELETE FROM Inventory i WHERE i.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}