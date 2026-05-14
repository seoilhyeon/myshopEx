package com.example.myShop.repository;

import com.example.myShop.entity.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>,
        ItemRepositoryCustom {

    List<Item> findByItemName(String itemName);

    @Query("select i from Item i where i.itemDetail like "
            + "%:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(String itemDetail);

    @Query(value = "select * from t_item i where i.item_detail like "
            + "%:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(String itemDetail);
}
