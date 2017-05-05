package org.shop.web.controller;

import org.shop.common.pojo.EUDataGridResult;
import org.shop.common.pojo.TaotaoResult;
import org.shop.manager.pojo.TbContent;
import org.shop.manager.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 内容管理Controller
 */
@Controller
@RequestMapping("/content")
public class ContentController {

	@Autowired
	private ContentService contentService;

	@RequestMapping("/save")
	@ResponseBody
	public TaotaoResult insertContent(TbContent content) {
		TaotaoResult result = contentService.insertContent(content);
		return result;
	}

	@RequestMapping("/query/list")
	@ResponseBody
	public EUDataGridResult getItemList(Integer page, Integer rows) {
		EUDataGridResult result = contentService.getItemList(page, rows);
		return result;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	private TaotaoResult deleteItem(@RequestBody String params)
			throws Exception {
		String id = params.substring(4, params.length());
		TaotaoResult result = contentService.deleteItem(id);
		return result;
	}
}
