package org.shop.portal.pojo;

import java.util.List;

import org.shop.manager.pojo.TbOrder;
import org.shop.manager.pojo.TbOrderItem;
import org.shop.manager.pojo.TbOrderShipping;

public class Order extends TbOrder {

	private List<TbOrderItem> orderItems;
	private TbOrderShipping orderShipping;

	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}

	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}

}
