package org.shop.portal.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.shop.common.pojo.TaotaoResult;
import org.shop.common.utils.CookieUtils;
import org.shop.common.utils.HttpClientUtil;
import org.shop.common.utils.JsonUtils;
import org.shop.manager.pojo.TbItem;
import org.shop.manager.pojo.TbUser;
import org.shop.portal.dao.JedisClient;
import org.shop.portal.pojo.CartItem;
import org.shop.portal.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 购物车Service
 */
@Service
public class CartServiceImpl implements CartService {

	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${ITME_INFO_URL}")
	private String ITME_INFO_URL;
	@Value("${REDIS_CART_SESSION_KEY}")
	private String REDIS_CART_SESSION_KEY;
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private UserServiceImpl userService;

	/**
	 * 添加购物车商品
	 */
	@Override
	public TaotaoResult addCartItem(long itemId, int num,
			HttpServletRequest request, HttpServletResponse response) {

		// 取商品信息
		CartItem cartItem = null;
		// 取购物车商品列表
		List<CartItem> itemList = getCartItemList(request, response);
		// 判断购物车商品列表中是否存在此商品
		for (CartItem cItem : itemList) {
			// 如果存在此商品longValue()
			if (cItem.getId() == itemId) {
				// 增加商品数量
				cItem.setNum(cItem.getNum() + num);
				cartItem = cItem;
				break;
			}
		}
		if (cartItem == null) {
			cartItem = new CartItem();
			// 根据商品id查询商品基本信息。
			String json = HttpClientUtil.doGet(REST_BASE_URL + ITME_INFO_URL
					+ itemId);
			// 把json转换成java对象
			TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json,
					TbItem.class);
			if (taotaoResult.getStatus() == 200) {
				TbItem item = (TbItem) taotaoResult.getData();
				cartItem.setId(item.getId());
				cartItem.setTitle(item.getTitle());
				cartItem.setImage(item.getImage() == null ? "" : item
						.getImage().split(",")[0]);
				cartItem.setNum(num);
				cartItem.setPrice(item.getPrice());
			}
			// 添加到购物车列表
			itemList.add(cartItem);
		}

		if (login(request)) {// 登录了
			// 将修改写入redis
			String tokenJson = CookieUtils.getCookieValue(request, "TT_TOKEN",
					true);
			TbUser tbUser = userService.getUserByToken(tokenJson);
			jedisClient.set(tbUser.getId().toString(),
					JsonUtils.objectToJson(itemList));
		} else {// 未登录
				// 把购物车列表写入cookie
			CookieUtils.setCookie(request, response, "TT_CART",
					JsonUtils.objectToJson(itemList), true);
		}
		return TaotaoResult.ok();
	}

	/**
	 * 删除购物车商品
	 */
	@Override
	public TaotaoResult deleteCartItem(long itemId, HttpServletRequest request,
			HttpServletResponse response) {
		// 从cookie中取购物车商品列表
		List<CartItem> itemList = getCartItemList(request, response);
		// 从列表中找到此商品
		for (CartItem cartItem : itemList) {
			if (cartItem.getId() == itemId) {
				itemList.remove(cartItem);
				break;
			}

		}
		if (login(request)) {// 登录了
			// 将修改写入redis
			String tokenJson = CookieUtils.getCookieValue(request, "TT_TOKEN",
					true);
			TbUser tbUser = userService.getUserByToken(tokenJson);
			jedisClient.set(tbUser.getId().toString(),
					JsonUtils.objectToJson(itemList));
		} else {// 未登录
				// 把购物车列表写入cookie
			CookieUtils.setCookie(request, response, "TT_CART",
					JsonUtils.objectToJson(itemList), true);
		}

		return TaotaoResult.ok();
	}

	/**
	 * 从cookie中取商品列表
	 */
	private List<CartItem> getCartItemList(HttpServletRequest request) {
		// 从cookie中取商品列表
		String cartJson = CookieUtils.getCookieValue(request, "TT_CART", true);
		if (cartJson == null || "".equals(cartJson)) {
			return new ArrayList<>();
		}
		// 把json转换成商品列表
		try {
			List<CartItem> list = JsonUtils
					.jsonToList(cartJson, CartItem.class);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public List<CartItem> getCartItemList(HttpServletRequest request,
			HttpServletResponse response) {
		List<CartItem> itemList = getCartItemList(request);
		// 登录了就从redis中取数据
		// 未登录就从cookie中取数据
		// 判断是否登录
		if (login(request)) {// 登录了
			String tokenJson = CookieUtils.getCookieValue(request, "TT_TOKEN",
					true);
			TbUser tbUser = userService.getUserByToken(tokenJson);
			String json = jedisClient.get(tbUser.getId().toString());
			if (StringUtils.isBlank(json) || "[]".equals(json)) {// redis为空
				if (itemList.size() > 0) {// cookie不为空
					return sync(itemList, null, request, response);
				} else {
					return itemList;
				}
			} else {// redis不为空
				List<CartItem> list = JsonUtils
						.jsonToList(json, CartItem.class);
				if (itemList.size() > 0) {// cookie不为空
					return sync(itemList, list, request, response);
				} else {
					return sync(null, list, request, response);
				}
			}
		} else {// 未登录
			return itemList;
		}
	}

	@Override
	public Boolean login(HttpServletRequest request) {
		String tokenJson = CookieUtils
				.getCookieValue(request, "TT_TOKEN", true);
		if (tokenJson == null || "".equals(tokenJson)) {
			return false;
		}
		return true;
	}

	@Override
	public List<CartItem> sync(List<CartItem> cookie, List<CartItem> redis,
			HttpServletRequest request, HttpServletResponse response) {
		String tokenJson = CookieUtils
				.getCookieValue(request, "TT_TOKEN", true);
		TbUser tbUser = userService.getUserByToken(tokenJson);
		String json = jedisClient.get(tbUser.getId().toString());
		// 1.redis为空,cookie不为空sync(cookie,null)
		if (redis == null || "".equals(redis) || "[]".equals(redis)) {
			jedisClient.set(tbUser.getId().toString(),
					JsonUtils.objectToJson(cookie));
			List<CartItem> list = JsonUtils.jsonToList(json, CartItem.class);
			CookieUtils.deleteCookie(request, response, "TT_CART");
			return list;
		}

		// 2.redis不为空，cookie为空sync(null, redis)
		if (cookie == null || "".equals(cookie)) {
			return redis;
		}

		// 3.redis，cookie都不为空sync(cookie, redis)
		// 判断购物车商品列表中是否存在此商品
		for (CartItem cItem : cookie) {
			for (CartItem cartItem2 : redis) {// redis
				// 如果存在此商品longValue()
				if (cItem.getId() == cartItem2.getId()) {
					// 增加商品数量
					cItem.setNum(cItem.getNum() + cartItem2.getNum());
					redis.remove(cartItem2);
					redis.add(cItem);
					break;
				} else {
					redis.add(cItem);
				}
			}
		}
		jedisClient.set(tbUser.getId().toString(),
				JsonUtils.objectToJson(redis));
		CookieUtils.deleteCookie(request, response, "TT_CART");
		String json2 = jedisClient.get(tbUser.getId().toString());
		List<CartItem> listItem = JsonUtils.jsonToList(json2, CartItem.class);
		return listItem;
	}
}
