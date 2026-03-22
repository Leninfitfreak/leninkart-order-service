package com.example.order.controller;

import com.example.order.model.OrderEntity;
import com.example.order.observability.BusinessMetrics;
import com.example.order.repo.OrderRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository repo;
    private final BusinessMetrics metrics;

    public OrderController(OrderRepository repo, BusinessMetrics metrics) {
        this.repo = repo;
        this.metrics = metrics;
    }

    @GetMapping
    public List<OrderEntity> all(@RequestAttribute("userId") String userId,
                                 @RequestAttribute(value = "role", required = false) String role) {
        metrics.recordOrderFetchRequest();
        try {
            if (userId == null || userId.isBlank()) {
                return Collections.emptyList();
            }
            if ("ADMIN".equalsIgnoreCase(role)) {
                return repo.findTop200ByOrderByIdDesc();
            }
            return repo.findTop200ByUserNameOrderByIdDesc(userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
