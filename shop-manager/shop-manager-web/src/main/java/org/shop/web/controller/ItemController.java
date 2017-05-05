package org.shop.web.controller;

import org.shop.common.pojo.EUDataGridResult;
import org.shop.common.pojo.TaotaoResult;
import org.shop.manager.pojo.TbItem;
import org.shop.manager.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 商品管理Controller
 */

@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;

	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem getItemById(@PathVariable Long itemId) {
		TbItem tbItem = itemService.getItemById(itemId);
		return tbItem;
	}

	@RequestMapping("/item/list")
	@ResponseBody
	public EUDataGridResult getItemList(Integer page, Integer rows) {
		EUDataGridResult result = itemService.getItemList(page, rows);
		return result;
	}

	@RequestMapping(value = "/item/save", method = RequestMethod.POST)
	@ResponseBody
	private TaotaoResult createItem(TbItem item, String desc, String itemParams)
			throws Exception {
		TaotaoResult result = itemService.createItem(item, desc, itemParams);
		return result;
	}

	@RequestMapping(value = "/item/update", method = RequestMethod.POST)
	@ResponseBody
	private TaotaoResult updateItem(TbItem item, String desc, String itemParams)
			throws Exception {
		TaotaoResult result = itemService.updateItem(item, desc, itemParams);
		return result;
	}

	@RequestMapping(value = "/item/delete", method = RequestMethod.POST)
	@ResponseBody
	private TaotaoResult deleteItem(@RequestBody String params)
			throws Exception {
		String id = params.substring(4, params.length());
		TaotaoResult result = itemService.deleteItem(id);
		return result;
	}
}
