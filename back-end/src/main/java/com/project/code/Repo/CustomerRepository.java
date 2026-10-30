package com.project.code.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.code.Model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /** Find a customer by email address. */
    Customer findByEmail(String email);

    /** Named "findByid" to avoid clashing with JpaRepository.findById. */
    Customer findByid(Long id);
}