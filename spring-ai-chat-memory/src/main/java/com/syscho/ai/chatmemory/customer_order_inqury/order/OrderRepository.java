package com.syscho.ai.chatmemory.customer_order_inqury.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    List<OrderEntity> findByCustomerId(String customerId);

    Optional<OrderEntity> findByOrderIdAndCustomerId(String orderId, String customerId);

    List<OrderEntity> findByCustomerIdAndStatus(String customerId, OrderStatus status);
}