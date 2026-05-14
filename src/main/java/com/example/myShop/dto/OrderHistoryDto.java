package com.example.myShop.dto;

import com.example.myShop.constant.OrderStatus;
import com.example.myShop.entity.Order;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderHistoryDto {

    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    public OrderHistoryDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }

    public void addOrderItemDto(OrderItemDto orderItemDto) {
        this.orderItemDtoList.add(orderItemDto);
    }
}
