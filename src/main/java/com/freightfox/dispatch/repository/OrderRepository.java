package com.freightfox.dispatch.repository;

import com.freightfox.dispatch.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    Optional<OrderEntity> findByOrderId(String orderId);
}
