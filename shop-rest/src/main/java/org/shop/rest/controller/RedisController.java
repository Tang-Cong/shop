package org.shop.rest.controller;

import org.shop.common.pojo.TaotaoResult;
import org.shop.rest.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 缓存同步Controller
 */
@Controller
@RequestMapping("/cache/sync")
public class RedisController {

	@Autowired
	private RedisService redisService;

	@RequestMapping("/content/{contentCid}")
	@ResponseBody
	public TaotaoResult contentCacheSync(@PathVariable Long contentCid) {
		TaotaoResult result = redisService.syncContent(contentCid);
		return result;
	}
}
