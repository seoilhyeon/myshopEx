package com.example.myShop.dto;

import com.example.myShop.entity.ItemImg;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Setter
@Getter
@NoArgsConstructor
public class ItemImgDto {

    private static ModelMapper modelMapper = new ModelMapper();
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;

    public static ItemImgDto of(ItemImg itemImg) {
        return modelMapper.map(itemImg, ItemImgDto.class);
    }
}
