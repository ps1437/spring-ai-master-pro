package com.syscho.ai.customer_support.bot.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {

    // ── Basic Finders ─────────────────────────────────────

    Optional<CustomerEntity> findByEmail(String email);

    Optional<CustomerEntity> findByPhone(String phone);

    Optional<CustomerEntity> findByActiveOrderId(String orderId);


    List<CustomerEntity> findByActiveTrue();

    List<CustomerEntity> findByActiveFalse();


    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);


    long countByActiveTrue();


}