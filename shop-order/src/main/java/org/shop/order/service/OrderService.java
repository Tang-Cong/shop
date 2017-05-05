package org.shop.order.service;

import java.util.List;

import org.shop.common.pojo.TaotaoResult;
import org.shop.manager.pojo.TbOrder;
import org.shop.manager.pojo.TbOrderItem;
import org.shop.manager.pojo.TbOrderShipping;

public interface OrderService {

	TaotaoResult createOrder(TbOrder order, List<TbOrderItem> itemList,
			TbOrderShipping orderShipping);
}
