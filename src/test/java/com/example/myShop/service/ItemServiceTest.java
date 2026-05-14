package com.example.myShop.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.myShop.annotation.IntegrationTest;
import com.example.myShop.annotation.WithMockMember;
import com.example.myShop.constant.ItemSellStatus;
import com.example.myShop.dto.ItemFormDto;
import com.example.myShop.entity.Item;
import com.example.myShop.entity.ItemImg;
import com.example.myShop.repository.ItemImgRepository;
import com.example.myShop.repository.ItemRepository;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@IntegrationTest
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultipartFiles() {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String path = "/Users/ilhyeon/Documents/study/devcourse/storage/shop";
            String imageName = "image" + i + ".jpg";
            MockMultipartFile multipartFile =
                    new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4});
            multipartFiles.add(multipartFile);
        }
        return multipartFiles;
    }

    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockMember
    void saveItem() throws Exception {
        ItemFormDto itemFormDto = ItemFormDto.builder()
                .itemName("테스트상품")
                .itemSellStatus(ItemSellStatus.SELL)
                .itemDetail("테스트 상품 입니다.")
                .price(1000)
                .stockNumber(100)
                .build();

        List<MultipartFile> multipartFileList = createMultipartFiles();
        Long itemId = itemService.saveItem(itemFormDto, multipartFileList);
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        assertThat(itemFormDto.getItemName()).isEqualTo(item.getItemName());
        assertThat(itemFormDto.getItemSellStatus()).isEqualTo(item.getItemSellStatus());
        assertThat(itemFormDto.getItemDetail()).isEqualTo(item.getItemDetail());
        assertThat(itemFormDto.getPrice()).isEqualTo(item.getPrice());
        assertThat(itemFormDto.getStockNumber()).isEqualTo(item.getStockNumber());
        assertThat(multipartFileList.get(0).getOriginalFilename()).isEqualTo(
                itemImgList.get(0).getOriImgName());
    }
}