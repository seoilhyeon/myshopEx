package com.example.myShop.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.myShop.annotation.IntegrationTest;
import com.example.myShop.dto.CartItemDto;
import com.example.myShop.entity.CartItem;
import com.example.myShop.entity.Item;
import com.example.myShop.entity.Member;
import com.example.myShop.repository.CartItemRepository;
import com.example.myShop.support.RepositoryPersistenceSupport;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class CartServiceTest extends RepositoryPersistenceSupport {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("장바구니 담기 테스트")
    void addCart() {
        Item item = persistItem();
        Member member = persistMember("test@test.com");

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCount(5);
        cartItemDto.setItemId(item.getId());

        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        assertThat(cartItem.getItem().getId()).isEqualTo(item.getId());
        assertThat(cartItem.getCount()).isEqualTo(cartItemDto.getCount());
    }
}