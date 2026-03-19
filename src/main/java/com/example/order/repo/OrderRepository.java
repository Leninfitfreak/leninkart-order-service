package com.example.order.repo;

import com.example.order.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findTop200ByOrderByIdDesc();
    List<OrderEntity> findTop200ByUserNameOrderByIdDesc(String userName);
}
