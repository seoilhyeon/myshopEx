package com.example.myShop.fixture;

import com.example.myShop.constant.OrderStatus;
import com.example.myShop.entity.Item;
import com.example.myShop.entity.Member;
import com.example.myShop.entity.Order;
import com.example.myShop.entity.OrderItem;
import java.time.LocalDateTime;
import java.util.List;

public class OrderFixture {

    private static final int DEFAULT_ITEM_COUNT = 10;
    private static final OrderStatus DEFAULT_ORDER_STATUS = OrderStatus.ORDER;

    private OrderFixture() {
    }

    public static Order createOrder() {
        return createOrder(null);
    }

    public static Order createOrder(Member member) {
        return createOrder(member, LocalDateTime.now(), DEFAULT_ORDER_STATUS);
    }

    public static Order createOrderWithItems(Member member, List<OrderItem> orderItems) {
        Order order = createOrder(member);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        return order;
    }

    public static OrderItem createOrderItem(Item item) {
        return createOrderItem(item, DEFAULT_ITEM_COUNT);
    }

    public static Order createOrder(Member member, LocalDateTime orderDate, OrderStatus orderStatus) {
        return Order.createOrder(member, orderDate, orderStatus);
    }

    public static OrderItem createOrderItem(Item item, int count) {
        return OrderItem.create(item, count);
    }
}
