package com.syscho.ai.chatmemory.customer_order_inqury;

import com.syscho.ai.chatmemory.customer_order_inqury.customer.CustomerEntity;
import com.syscho.ai.chatmemory.customer_order_inqury.customer.CustomerRepository;
import com.syscho.ai.chatmemory.customer_order_inqury.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupportChatService {

    private final ChatClient supportChatClient;
    private final OrderService orderService;
    private final CustomerRepository customerRepository;


    public String chat(ChatRequest request) {
        String customerId = request.getCustomerId();
        String userMessage = request.getMessage();

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("invalid customer id"));

        String orderSummary = orderService.getOrderSummary(customerId);

        String actionResult = handleIntent(userMessage, customer);

        String enrichedMessage = actionResult != null
                ? userMessage + "\n[SYSTEM ACTION TAKEN: " + actionResult + "]"
                : userMessage;

        return supportChatClient.prompt()
                .system(spec -> spec
                        .param("company_name", "ShopEasy")
                        .param("customer_name", customer.getName())
                        .param("customer_email", customer.getEmail())
                        .param("customer_tier", customer.getTier())
                        .param("active_order_id", customer.getActiveOrderId())
                        .param("order_summary", orderSummary)
                        .param("refund_policy", "30-day no questions asked")
                        .param("max_discount", "15")
                        .param("response_time", "24")
                        .param("support_hours", "Mon–Fri, 9AM–6PM IST")
                        .param("current_date", LocalDate.now().toString())
                )
                .user(enrichedMessage)
                .advisors(advisor -> advisor
                        .param(ChatMemory.CONVERSATION_ID, customerId)
                )
                .call()
                .content();
    }

    private String handleIntent(String message, CustomerEntity customer) {
        String lower = message.toLowerCase();

        if (lower.contains("cancel") || lower.contains("return")) {
            String result = orderService.cancelOrFlagReturn(
                    customer.getActiveOrderId());
            if ("RETURN_FLAGGED".equals(result)) {
                orderService.initiateRefund(customer.getActiveOrderId());
            }
            return result;
        }

        if (lower.matches(".*(yes|proceed|sure|ok|confirm|go ahead).*")) {
            orderService.initiateRefund(customer.getActiveOrderId());
            return "REFUND_CONFIRMED";
        }

        return null;
    }

}