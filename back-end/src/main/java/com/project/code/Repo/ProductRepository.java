package com.project.code.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAll();

    /** Filter products by category (derived query). */
    List<Product> findByCategory(String category);

    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    List<Product> findBySku(String sku);

    Product findByName(String name);

    /** Named "findByid" so it doesn't clash with JpaRepository.findById (which returns Optional). */
    Product findByid(Long id);

    /** Products in a store whose name contains the text (case-insensitive). */
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId "
         + "AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Product> findByNameLike(@Param("storeId") Long storeId, @Param("pname") String pname);

    /** Products in a store matching both name text and category. */
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId "
         + "AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%')) "
         + "AND i.product.category = :category")
    List<Product> findByNameAndCategory(@Param("storeId") Long storeId,
                                        @Param("pname") String pname,
                                        @Param("category") String category);

    /** Products in a store filtered by category (store ID and category). */
    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId "
         + "AND i.product.category = :category")
    List<Product> findByCategoryAndStoreId(@Param("storeId") Long storeId,
                                           @Param("category") String category);

    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Product> findProductBySubName(@Param("pname") String pname);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId")
    List<Product> findProductsByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT i.product FROM Inventory i WHERE i.product.category = :category "
         + "AND i.store.id = :storeId")
    List<Product> findProductByCategory(@Param("category") String category,
                                        @Param("storeId") Long storeId);

    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%')) "
         + "AND i.category = :category")
    List<Product> findProductBySubNameAndCategory(@Param("pname") String pname,
                                                  @Param("category") String category);
}