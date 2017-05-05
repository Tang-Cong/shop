package org.shop.manager.service;

import org.shop.common.pojo.EUDataGridResult;
import org.shop.common.pojo.TaotaoResult;
import org.shop.manager.pojo.TbContent;

public interface ContentService {

	TaotaoResult insertContent(TbContent content);

	EUDataGridResult getItemList(int page, int rows);

	TaotaoResult deleteItem(String id);
}
