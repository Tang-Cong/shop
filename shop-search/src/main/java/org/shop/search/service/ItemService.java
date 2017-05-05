package org.shop.search.service;

import org.shop.common.pojo.TaotaoResult;

public interface ItemService {

	TaotaoResult importAllItems();

	TaotaoResult updateItem(String id);
}
