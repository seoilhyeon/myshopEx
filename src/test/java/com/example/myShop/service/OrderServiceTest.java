package com.example.myShop.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.myShop.annotation.IntegrationTest;
import com.example.myShop.constant.OrderStatus;
import com.example.myShop.dto.OrderDto;
import com.example.myShop.entity.Item;
import com.example.myShop.entity.Member;
import com.example.myShop.entity.Order;
import com.example.myShop.entity.OrderItem;
import com.example.myShop.repository.ItemRepository;
import com.example.myShop.repository.MemberRepository;
import com.example.myShop.repository.OrderRepository;
import com.example.myShop.support.RepositoryPersistenceSupport;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class OrderServiceTest extends RepositoryPersistenceSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("주문 테스트")
    void order() {
        Item item = persistItem();
        Member member = persistMember("test@test.com");

        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(item.getId());
        orderDto.setCount(10);

        Long orderId = orderService.order(orderDto, member.getEmail());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        List<OrderItem> orderItems = order.getOrderItems();
        int totalPrice = orderDto.getCount() * item.getPrice();
        assertThat(order.getTotalPrice()).isEqualTo(totalPrice);
        assertThat(orderItems).hasSize(1);
    }

    @Test
    @DisplayName("주문 취소 테스트")
    void cancelOrder() {
        Item item = persistItem();
        Member member = persistMember("test@test.com");

        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(item.getId());
        orderDto.setCount(10);
        Long orderId = orderService.order(orderDto, member.getEmail());

        orderService.cancelOrder(orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(item.getStockNumber()).isEqualTo(100);
    }
}