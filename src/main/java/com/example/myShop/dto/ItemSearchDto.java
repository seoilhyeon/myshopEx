package com.example.myShop.dto;

import com.example.myShop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ItemSearchDto {

    private String searchDateType;
    private ItemSellStatus searchSellStatus;
    private String searchBy;
    private String searchQuery = "";
}
