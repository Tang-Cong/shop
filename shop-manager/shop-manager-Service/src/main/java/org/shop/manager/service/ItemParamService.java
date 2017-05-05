package org.shop.manager.service;

import org.shop.common.pojo.EUDataGridResult;
import org.shop.common.pojo.TaotaoResult;
import org.shop.manager.pojo.TbItemParam;

public interface ItemParamService {

	TaotaoResult getItemParamByCid(long cid);

	TaotaoResult insertItemParam(TbItemParam itemParam);

	EUDataGridResult getItemParamList(int page, int rows);

	TaotaoResult deleteItemParam(String id);
}
