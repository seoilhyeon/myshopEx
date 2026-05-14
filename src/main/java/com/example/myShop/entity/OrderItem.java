package com.example.myShop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_orderitem")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;
    private int count;

    @Builder(access = AccessLevel.PRIVATE)
    private OrderItem(Item item, Order order, int orderPrice, int count) {
        this.item = item;
        this.order = order;
        this.orderPrice = orderPrice;
        this.count = Math.max(count, 0);
    }

    public static OrderItem create(Item item, int count) {
        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .count(count)
                .orderPrice(item.getPrice())
                .build();
        item.removeStock(count);
        return orderItem;
    }

    public void cancel() {
        this.getItem().addStock(count);
    }

    public int getTotalPrice() {
        return orderPrice * count;
    }
}
