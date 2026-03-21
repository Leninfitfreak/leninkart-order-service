package com.example.order.kafka;

import com.example.order.model.OrderEntity;
import com.example.order.observability.StructuredLog;
import com.example.order.repo.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OrderConsumer {
    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    private final OrderRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrderConsumer(OrderRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = "product-orders", groupId = "order-group")
    public void listen(
        String message,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset
    ) throws Exception {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("topic", topic);
        fields.put("partition", partition);
        fields.put("offset", offset);
        fields.put("operation_kind", "kafka_consumer");

        StructuredLog.info(logger, "kafka.consume.product-orders", "Received order event", fields);

        try {
            Map<String, Object> payload = mapper.readValue(message, Map.class);
            Long productId = Long.valueOf(String.valueOf(payload.get("productId")));
            String name = String.valueOf(payload.get("name"));
            Double price = Double.valueOf(String.valueOf(payload.get("price")));
            String user = String.valueOf(payload.getOrDefault("user", "anonymous"));

            OrderEntity order = new OrderEntity(productId, name, price, "CREATED");
            order.setUserName(user);
            OrderEntity saved = repo.save(order);

            Map<String, Object> successFields = new LinkedHashMap<>(fields);
            successFields.put("product_id", productId);
            successFields.put("order_id", saved.getId());
            successFields.put("user", user);
            StructuredLog.info(logger, "kafka.consume.product-orders", "Order event processed", successFields);
        } catch (Exception ex) {
            Map<String, Object> errorFields = new LinkedHashMap<>(fields);
            errorFields.put("payload_preview", message);
            StructuredLog.error(logger, "kafka.consume.product-orders", "Order event processing failed", ex, errorFields);
            throw ex;
        }
    }
}
