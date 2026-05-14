package com.example.myShop.repository;

import com.example.myShop.constant.ItemSellStatus;
import com.example.myShop.dto.ItemSearchDto;
import com.example.myShop.dto.MainItemDto;
import com.example.myShop.dto.QMainItemDto;
import com.example.myShop.entity.Item;
import com.example.myShop.entity.QItem;
import com.example.myShop.entity.QItemImg;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        List<Item> content = queryFactory.selectFrom(QItem.item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                )
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory.select(Wildcard.count)
                        .from(QItem.item)
                        .where(
                                regDtsAfter(itemSearchDto.getSearchDateType()),
                                searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                                searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                        )
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = queryFactory.select(
                        new QMainItemDto(
                                item.id,
                                item.itemName,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price
                        )
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNameLike(itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory.select(Wildcard.count)
                        .from(itemImg)
                        .join(itemImg.item, item)
                        .where(itemImg.repImgYn.eq("Y"))
                        .where(itemNameLike(itemSearchDto.getSearchQuery()))
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression itemNameLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemName.contains(searchQuery);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();
        if (searchDateType == null || "all".equals(searchDateType)) {
            return null;
        } else if ("1d".equals(searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if ("1w".equals(searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if ("1m".equals(searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if ("6m".equals(searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (!StringUtils.hasText(searchQuery)) {
            return null;
        }
        if ("itemName".equals(searchBy)) {
            return QItem.item.itemName.contains(searchQuery);
        }
        if ("createdBy".equals(searchBy)) {
            return QItem.item.createdBy.contains(searchQuery);
        }
        return null;
    }
}
