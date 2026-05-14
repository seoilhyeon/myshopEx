package com.example.myShop.fixture;

import com.example.myShop.constant.ItemSellStatus;
import com.example.myShop.entity.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemFixture {

    private static final String DEFAULT_ITEM_NAME = "테스트 상품";
    private static final int DEFAULT_PRICE = 10000;
    private static final int DEFAULT_STOCK = 100;
    private static final String DEFAULT_ITEM_DETAIL = "상세설명";
    private static final ItemSellStatus DEFAULT_SELL_STATUS = ItemSellStatus.SELL;

    private ItemFixture() {
    }

    public static List<Item> createDefaultItemList() {
        List<Item> itemList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Item item = createItem(
                    DEFAULT_ITEM_NAME + i,
                    DEFAULT_PRICE + i,
                    DEFAULT_STOCK,
                    "테스트 상품 상세 설명" + i,
                    DEFAULT_SELL_STATUS
            );

            itemList.add(item);
        }
        return itemList;
    }

    public static Item createItem() {
        return createItem(
                DEFAULT_ITEM_NAME,
                DEFAULT_PRICE,
                DEFAULT_STOCK,
                DEFAULT_ITEM_DETAIL,
                DEFAULT_SELL_STATUS
        );
    }

    public static Item createItem(String itemName) {
        return createItem(
                itemName,
                DEFAULT_PRICE,
                DEFAULT_STOCK,
                DEFAULT_ITEM_DETAIL,
                DEFAULT_SELL_STATUS
        );
    }

    public static Item createItem(
            String itemName,
            int price,
            int stockNumber,
            String itemDetail,
            ItemSellStatus itemSellStatus
    ) {
        return Item.builder()
                .itemName(itemName)
                .price(price)
                .stockNumber(stockNumber)
                .itemDetail(itemDetail)
                .itemSellStatus(itemSellStatus)
                .build();
    }
}
