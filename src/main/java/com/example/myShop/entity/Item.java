package com.example.myShop.entity;

import com.example.myShop.constant.ItemSellStatus;
import com.example.myShop.dto.ItemFormDto;
import com.example.myShop.exception.OutOfStockException;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.Assert;

@Entity
@Table(name = "t_item")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber;

    @Lob
    @Column(nullable = false)
    private String itemDetail;

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;

    @Builder
    private Item(String itemName, int price, int stockNumber, String itemDetail,
            ItemSellStatus itemSellStatus) {
        this.itemName = itemName;
        this.price = price;
        this.stockNumber = stockNumber;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Item)) {
            return false;
        }
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void removeStock(int count) {
        int restStock = stockNumber - count;
        if (restStock < 0) {
            throw new OutOfStockException("상품의 재고가 부족합니다.(현재 재고 수량:" + this.stockNumber + ")");
        }
        stockNumber = restStock;
    }

    public void addStock(int stockNumber) {
        Assert.isTrue(stockNumber > 0, "유효하지 않은 추가 재고 수량입니다.");
        this.stockNumber += stockNumber;
    }

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemName = itemFormDto.getItemName();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }
}
