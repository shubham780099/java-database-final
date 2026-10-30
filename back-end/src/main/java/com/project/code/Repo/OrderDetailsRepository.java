package com.project.code.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.code.Model.OrderDetails;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
}