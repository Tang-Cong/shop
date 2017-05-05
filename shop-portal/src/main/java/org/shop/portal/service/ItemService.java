package org.shop.portal.service;

import org.shop.portal.pojo.ItemInfo;

public interface ItemService {

	ItemInfo getItemById(Long itemId);

	String getItemDescById(Long itemId);

	String getItemParam(Long itemId);

}
