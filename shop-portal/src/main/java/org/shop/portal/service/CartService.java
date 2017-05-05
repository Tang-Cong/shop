package org.shop.portal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shop.common.pojo.TaotaoResult;
import org.shop.portal.pojo.CartItem;

public interface CartService {

	TaotaoResult addCartItem(long itemId, int num, HttpServletRequest request,
			HttpServletResponse response);

	List<CartItem> getCartItemList(HttpServletRequest request,
			HttpServletResponse response);

	TaotaoResult deleteCartItem(long itemId, HttpServletRequest request,
			HttpServletResponse response);

	Boolean login(HttpServletRequest request);

	List<CartItem> sync(List<CartItem> cookie, List<CartItem> redis,
			HttpServletRequest request, HttpServletResponse response);

}