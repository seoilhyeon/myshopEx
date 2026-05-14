package com.example.myShop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.myShop.annotation.IntegrationTest;
import com.example.myShop.entity.Cart;
import com.example.myShop.entity.Member;
import com.example.myShop.support.RepositoryPersistenceSupport;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class CartRepositoryTest extends RepositoryPersistenceSupport {

    @Autowired
    private CartRepository cartRepository;

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    void findCartAndMemberTest() {
        Member member = persistMember();
        Cart cart = Cart.createCart(member);
        cartRepository.save(cart);
        flushAndClear();

        Cart savedCart = cartRepository.findById(cart.getId())
                .orElseThrow(EntityNotFoundException::new);

        assertThat(savedCart.getMember().getId()).isEqualTo(member.getId());
    }
}
