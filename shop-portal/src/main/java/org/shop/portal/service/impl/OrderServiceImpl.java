package org.shop.portal.service.impl;

import org.shop.common.pojo.TaotaoResult;
import org.shop.common.utils.HttpClientUtil;
import org.shop.common.utils.JsonUtils;
import org.shop.portal.pojo.Order;
import org.shop.portal.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 订单处理Service
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Value("${ORDER_BASE_URL}")
	private String ORDER_BASE_URL;
	@Value("${ORDER_CREATE_URL}")
	private String ORDER_CREATE_URL;

	@Override
	public String createOrder(Order order) {
		// 调用创建订单服务之前补全用户信息。
		// 从cookie中后取TT_TOKEN的内容，根据token调用sso系统的服务根据token换取用户信息。

		// 调用taotao-order的服务提交订单。
		String json = HttpClientUtil.doPostJson(ORDER_BASE_URL
				+ ORDER_CREATE_URL, JsonUtils.objectToJson(order));
		// 把json转换成taotaoResult
		TaotaoResult taotaoResult = TaotaoResult.format(json);
		if (taotaoResult.getStatus() == 200) {
			Object orderId = taotaoResult.getData();
			return orderId.toString();
		}
		return "";
	}

}