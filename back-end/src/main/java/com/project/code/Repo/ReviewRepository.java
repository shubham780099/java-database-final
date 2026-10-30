package com.project.code.Repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.project.code.Model.Review;

public interface ReviewRepository extends MongoRepository<Review, String> {

    /** Find reviews for one product in one store. */
    List<Review> findByStoreIdAndProductId(Long storeId, Long productId);
}