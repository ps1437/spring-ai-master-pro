package com.syscho.ai.customer_support.bot.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderEntity getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + orderId));
    }

    public OrderEntity getLatestOrder(String customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .max(Comparator.comparing(OrderEntity::getOrderedAt))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No orders found for customer: " + customerId));
    }

    public String getOrderSummary(String customerId) {
        OrderEntity orderEntity = getLatestOrder(customerId);
        return String.format(
                "Order ID: %s | Product: %s | Status: %s | " +
                        "Estimated Delivery: %s | Amount: ₹%.2f",
                orderEntity.getOrderId(),
                orderEntity.getProductName(),
                orderEntity.getStatus(),
                orderEntity.getEstimatedDelivery().toLocalDate(),
                orderEntity.getAmount()
        );
    }

    public String cancelOrFlagReturn(String orderId) {
        OrderEntity orderEntity = getOrder(orderId);

        if (orderEntity.getStatus() == OrderStatus.OUT_FOR_DELIVERY
                || orderEntity.getStatus() == OrderStatus.SHIPPED) {
            orderEntity.setStatus(OrderStatus.RETURN_REQUESTED);
            orderRepository.save(orderEntity);
            log.info("Order {} flagged for return on arrival", orderId);
            return "RETURN_FLAGGED";
        }

        orderEntity.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(orderEntity);
        log.info("Order {} cancelled", orderId);
        return "CANCELLED";
    }

    public void initiateRefund(String orderId) {
        OrderEntity orderEntity = getOrder(orderId);
        orderEntity.setStatus(OrderStatus.REFUND_INITIATED);
        orderRepository.save(orderEntity);
        log.info("Refund initiated for order {}", orderId);
    }
}