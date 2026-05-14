package com.example.myShop.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class ItemDto {

    private String itemDetail;
    private String itemName;
    private int price;
    private LocalDateTime regTime;

    @Builder
    private ItemDto(String itemDetail, String itemName, int price, LocalDateTime regTime) {
        this.itemDetail = itemDetail;
        this.itemName = itemName;
        this.price = price;
        this.regTime = regTime;
    }
}
