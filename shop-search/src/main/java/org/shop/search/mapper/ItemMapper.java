package org.shop.search.mapper;

import java.util.List;

import org.shop.search.pojo.Item;

public interface ItemMapper {

	List<Item> getItemList();

	Item selectById(String id);
}
