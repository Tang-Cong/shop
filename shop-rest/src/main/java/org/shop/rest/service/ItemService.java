package org.shop.rest.service;

import org.shop.common.pojo.TaotaoResult;

public interface ItemService {

	TaotaoResult getItemBaseInfo(long itemId);

	TaotaoResult getItemDesc(long itemId);

	TaotaoResult getItemParam(long itemId);
}
