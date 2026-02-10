package com.example.order.repo;

import com.example.order.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByOrderByIdDesc();
    List<OrderEntity> findAllByUserNameOrderByIdDesc(String userName);
}
