package com.example.myShop.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartOrderDto {

    private Long cartItemId;
    private List<CartOrderDto> cartOrderDtoList;
}
