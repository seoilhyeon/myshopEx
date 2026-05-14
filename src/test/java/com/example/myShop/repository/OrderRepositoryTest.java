package com.example.myShop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.myShop.annotation.H2RepositoryTest;
import com.example.myShop.entity.Order;
import com.example.myShop.entity.OrderItem;
import com.example.myShop.support.RepositoryPersistenceSupport;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@H2RepositoryTest
class OrderRepositoryTest extends RepositoryPersistenceSupport {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("영속성 전이 테스트")
    void cascadeTest() {
        Order order = persistOrderWithItems(3);
        Order savedOrder = reloadOrder(order.getId());

        assertThat(savedOrder.getOrderItems().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    void orphanRemovalTest() {
        Order order = persistOrderWithItems(3);
        order.removeOrderItem(0);
        flushAndClear();
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    void lazyLoadingTest() {
        Order order = persistOrderWithItems(3);
        Long orderItemId = order.getOrderItems().get(0).getId();
        flushAndClear();
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println(orderItem);
    }
}
