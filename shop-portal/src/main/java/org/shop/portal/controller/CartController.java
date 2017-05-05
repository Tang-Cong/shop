package org.shop.portal.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shop.common.pojo.TaotaoResult;
import org.shop.portal.pojo.CartItem;
import org.shop.portal.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 购物车Controller
 */
@Controller
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	@RequestMapping("/add/{itemId}")
	public String addCartItem(@PathVariable Long itemId,
			@RequestParam(defaultValue = "1") Integer num,
			HttpServletRequest request, HttpServletResponse response) {
		TaotaoResult result = cartService.addCartItem(itemId, num, request,
				response);
		return "redirect:/cart/success.html";
	}

	@RequestMapping("/success")
	public String showSuccess() {
		return "cartSuccess";
	}

	@RequestMapping("/cart")
	public String showCart(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		List<CartItem> list = cartService.getCartItemList(request, response);
		model.addAttribute("cartList", list);
		return "cart";
	}

	@RequestMapping("/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId,
			HttpServletRequest request, HttpServletResponse response) {
		cartService.deleteCartItem(itemId, request, response);
		return "redirect:/cart/cart.html";
	}

	@RequestMapping("/add/{itemId}/{num}")
	@ResponseBody
	public TaotaoResult updateCartItemNum(@PathVariable long itemId,
			@PathVariable int num, HttpServletRequest request,
			HttpServletResponse response) {
		TaotaoResult result = cartService.addCartItem(itemId, num, request,
				response);
		return result;

	}

}
