package com.example.myShop.entity;

import com.example.myShop.constant.OrderStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "kakao_tid")
    private String kakaoTid;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Order(Member member, LocalDateTime orderDate, OrderStatus orderStatus) {
        this.member = member;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    public static Order createOrder(Member member, List<OrderItem> orderItems) {
        Order order = createOrder(member, LocalDateTime.now(), OrderStatus.ORDER);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        return order;
    }

    public static Order createOrder(Member member, LocalDateTime orderDate, OrderStatus orderStatus) {
        return Order.builder()
                .member(member)
                .orderDate(orderDate)
                .orderStatus(orderStatus)
                .build();
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItem orderItem) {
        this.orderItems.remove(orderItem);
        orderItem.setOrder(null);
        orderItem.getItem().addStock(orderItem.getCount());
    }

    public void removeOrderItem(int idx) {
        OrderItem remove = this.orderItems.remove(idx);
        remove.setOrder(null);
        remove.getItem().addStock(remove.getCount());
    }

    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void updateKakaoTid(String kakaoTid) {
        this.kakaoTid = kakaoTid;
    }
}
