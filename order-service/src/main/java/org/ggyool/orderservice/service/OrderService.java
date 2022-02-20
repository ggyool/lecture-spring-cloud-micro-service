package org.ggyool.orderservice.service;

import org.ggyool.orderservice.dto.OrderDto;
import org.ggyool.orderservice.entity.OrderEntity;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);

    Iterable<OrderEntity> getOrdersByUserId(String userId);

    OrderDto getOrderByOrderId(String orderId);
}
