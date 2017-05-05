package org.shop.manager.service;

import org.shop.common.pojo.EUDataGridResult;
import org.shop.common.pojo.TaotaoResult;
import org.shop.manager.pojo.TbItem;

public interface ItemService {

	TbItem getItemById(long itemId);

	EUDataGridResult getItemList(int page, int rows);

	TaotaoResult createItem(TbItem item, String desc, String itemParam)
			throws Exception;

	TaotaoResult updateItem(TbItem item, String desc, String itemParam)
			throws Exception;

	TaotaoResult deleteItem(String id);

}
