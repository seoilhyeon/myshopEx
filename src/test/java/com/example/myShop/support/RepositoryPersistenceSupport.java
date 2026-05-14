package com.example.myShop.support;

import com.example.myShop.entity.Item;
import com.example.myShop.entity.Member;
import com.example.myShop.entity.Order;
import com.example.myShop.entity.OrderItem;
import com.example.myShop.fixture.ItemFixture;
import com.example.myShop.fixture.MemberFixture;
import com.example.myShop.fixture.OrderFixture;
import com.example.myShop.repository.ItemRepository;
import com.example.myShop.repository.MemberRepository;
import com.example.myShop.repository.OrderRepository;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class RepositoryPersistenceSupport extends JpaTestSupport {

    private static final AtomicLong MEMBER_SEQUENCE = new AtomicLong();

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected OrderRepository orderRepository;

    protected Item persistItem() {
        return persistItem(ItemFixture.createItem());
    }

    protected Item persistItem(String itemName) {
        return persistItem(ItemFixture.createItem(itemName));
    }

    protected Item persistItem(Item item) {
        return itemRepository.save(item);
    }

    protected Member persistMember() {
        return persistMember(nextMemberEmail());
    }

    protected Member persistMember(String email) {
        return persistMember(MemberFixture.createMember(email));
    }

    protected Member persistMember(Member member) {
        return memberRepository.save(member);
    }

    protected Order persistOrder(Order order) {
        return orderRepository.save(order);
    }

    protected Order persistOrderWithItems(int orderItemCount) {
        Order order = OrderFixture.createOrder(persistMember());

        for (int i = 0; i < orderItemCount; i++) {
            Item item = persistItem("테스트 상품" + i);
            OrderItem orderItem = OrderFixture.createOrderItem(item);
            order.addOrderItem(orderItem);
        }

        return orderRepository.saveAndFlush(order);
    }

    protected Order reloadOrder(Long orderId) {
        flushAndClear();
        return orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
    }

    protected String nextMemberEmail() {
        return "member" + MEMBER_SEQUENCE.incrementAndGet() + "@example.com";
    }
}
