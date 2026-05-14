package com.example.myShop.repository;

import static com.example.myShop.fixture.ItemFixture.createDefaultItemList;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.myShop.annotation.H2RepositoryTest;
import com.example.myShop.constant.ItemSellStatus;
import com.example.myShop.dto.ItemSearchDto;
import com.example.myShop.entity.Item;
import com.example.myShop.entity.QItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@H2RepositoryTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Test
    @DisplayName("상품 저장 테스트")
    void createItem() {
        Item item = Item.builder()
                .itemName("테스트 상품")
                .price(10000)
                .itemDetail("테스트 상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SELL)
                .stockNumber(100)
                .build();

        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem);
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    void findByItemNmTest() {
        itemRepository.saveAll(createDefaultItemList());

        List<Item> itemList = itemRepository.findByItemName("테스트 상품1");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    void findByItemDetailTest() {
        itemRepository.saveAll(createDefaultItemList());

        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    void findByItemDetailByNative() {
        itemRepository.saveAll(createDefaultItemList());

        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    void queryDslTest() {
        itemRepository.saveAll(createDefaultItemList());
        QItem qItem = QItem.item;

        List<Item> itemList = jpaQueryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc())
                .fetch();

        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("빈 등록자 검색어는 검색 조건에서 제외한다")
    void getAdminItemPageWithBlankCreatedBySearchQuery() {
        itemRepository.saveAll(createDefaultItemList());

        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setSearchDateType("all");
        itemSearchDto.setSearchBy("createdBy");
        itemSearchDto.setSearchQuery("");

        Page<Item> result = itemRepository.getAdminItemPage(itemSearchDto, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getTotalElements()).isEqualTo(5);
    }
}
