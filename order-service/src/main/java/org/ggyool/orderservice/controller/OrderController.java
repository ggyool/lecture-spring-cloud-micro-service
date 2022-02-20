package org.ggyool.orderservice.controller;

import org.ggyool.orderservice.dto.OrderDto;
import org.ggyool.orderservice.entity.OrderEntity;
import org.ggyool.orderservice.service.OrderService;
import org.ggyool.orderservice.vo.RequestOrder;
import org.ggyool.orderservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/order-service")
public class OrderController {

    private final OrderService orderService;
    private final ModelMapper modelMapper;

    public OrderController(OrderService orderService, ModelMapper modelMapper) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrdersByUserId(@PathVariable String userId) {
        Iterable<OrderEntity> orders = orderService.getOrdersByUserId(userId);
        List<ResponseOrder> responseOrders = StreamSupport.stream(orders.spliterator(), false)
                .map(orderEntity -> modelMapper.map(orderEntity, ResponseOrder.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseOrders);
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@RequestBody RequestOrder requestOrder,
                                                     @PathVariable String userId) {
        OrderDto orderDto = modelMapper.map(requestOrder, OrderDto.class);
        orderDto.setUserId(userId);
        return ResponseEntity.ok(
                modelMapper.map(orderService.createOrder(orderDto), ResponseOrder.class)
        );
    }

    @GetMapping("/health-check")
    public String status(@Value("${local.server.port}") String port) {
        return String.format("It's Working in Order Service on Port %s", port);
    }
}
