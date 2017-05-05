package org.shop.search.controller;

import org.shop.common.pojo.TaotaoResult;
import org.shop.search.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 索引库维护
 */
@Controller
@RequestMapping("/manager")
public class ItemController {

	@Autowired
	private ItemService itemService;

	/**
	 * 导入商品数据到索引库
	 */
	@RequestMapping("/importall")
	@ResponseBody
	public TaotaoResult importAllItems() {
		TaotaoResult result = itemService.importAllItems();
		return result;
	}

	@RequestMapping(value = "/updateById", method = RequestMethod.POST)
	@ResponseBody
	public TaotaoResult updateById(@RequestBody String params) {
		String id = params.substring(4, params.length());
		TaotaoResult result = itemService.updateItem(id);
		return result;
	}
}
