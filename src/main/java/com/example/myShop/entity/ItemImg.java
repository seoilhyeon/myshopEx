package com.example.myShop.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Entity
@Table(name = "item_img")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImg extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgName; //이미지 파일명
    private String oriImgName; //원본 이미지 파일명
    private String imgUrl; //이미지 조회 경로
    private String repImgYn; //대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Builder
    private ItemImg(String imgName, String oriImgName, String imgUrl, String repImgYn, Item item) {
        this.imgName = imgName;
        this.oriImgName = oriImgName;
        this.imgUrl = imgUrl;
        this.repImgYn = repImgYn;
        this.item = item;
    }

    public void updateItemImg(String oriImgName, String imgName, String imgUrl) {
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
