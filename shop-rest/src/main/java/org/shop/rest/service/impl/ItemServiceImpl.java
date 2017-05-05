package org.shop.rest.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.shop.common.pojo.TaotaoResult;
import org.shop.common.utils.JsonUtils;
import org.shop.manager.dao.TbItemDescMapper;
import org.shop.manager.dao.TbItemMapper;
import org.shop.manager.dao.TbItemParamItemMapper;
import org.shop.manager.pojo.TbItem;
import org.shop.manager.pojo.TbItemDesc;
import org.shop.manager.pojo.TbItemParamItem;
import org.shop.manager.pojo.TbItemParamItemExample;
import org.shop.manager.pojo.TbItemParamItemExample.Criteria;
import org.shop.rest.dao.JedisClient;
import org.shop.rest.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 商品信息管理Service
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbItemDescMapper itemDescMapper;

	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;

	@Value("${REDIS_ITEM_KEY}")
	private String REDIS_ITEM_KEY;
	@Value("${REDIS_ITEM_EXPIRE}")
	private Integer REDIS_ITEM_EXPIRE;

	@Autowired
	private JedisClient jedisClient;

	@Override
	public TaotaoResult getItemBaseInfo(long itemId) {
		try {
			// 添加缓存逻辑
			// 从缓存中取商品信息，商品id对应的信息
			String json = jedisClient.get(REDIS_ITEM_KEY + ":" + itemId
					+ ":base");
			// 判断是否有值
			if (!StringUtils.isBlank(json)) {
				// 把json转换成java对象
				TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
				return TaotaoResult.ok(item);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 根据商品id查询商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		// 使用TaotaoResult包装一下
		try {
			// 把商品信息写入缓存
			jedisClient.set(REDIS_ITEM_KEY + ":" + itemId + ":base",
					JsonUtils.objectToJson(item));
			// 设置key的有效期
			jedisClient.expire(REDIS_ITEM_KEY + ":" + itemId + ":base",
					REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return TaotaoResult.ok(item);
	}

	@Override
	public TaotaoResult getItemDesc(long itemId) {
		// 添加缓存
		try {
			// 添加缓存逻辑
			// 从缓存中取商品信息，商品id对应的信息
			String json = jedisClient.get(REDIS_ITEM_KEY + ":" + itemId
					+ ":desc");
			// 判断是否有值
			if (!StringUtils.isBlank(json)) {
				// 把json转换成java对象
				TbItemDesc itemDesc = JsonUtils.jsonToPojo(json,
						TbItemDesc.class);
				return TaotaoResult.ok(itemDesc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 创建查询条件
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);

		try {
			// 把商品信息写入缓存
			jedisClient.set(REDIS_ITEM_KEY + ":" + itemId + ":desc",
					JsonUtils.objectToJson(itemDesc));
			// 设置key的有效期
			jedisClient.expire(REDIS_ITEM_KEY + ":" + itemId + ":desc",
					REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return TaotaoResult.ok(itemDesc);
	}

	@Override
	public TaotaoResult getItemParam(long itemId) {
		// 添加缓存
		try {
			// 添加缓存逻辑
			// 从缓存中取商品信息，商品id对应的信息
			String json = jedisClient.get(REDIS_ITEM_KEY + ":" + itemId
					+ ":param");
			// 判断是否有值
			if (!StringUtils.isBlank(json)) {
				// 把json转换成java对象
				TbItemParamItem paramItem = JsonUtils.jsonToPojo(json,
						TbItemParamItem.class);
				return TaotaoResult.ok(paramItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 根据商品id查询规格参数
		// 设置查询条件
		TbItemParamItemExample example = new TbItemParamItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andItemIdEqualTo(itemId);
		// 执行查询
		List<TbItemParamItem> list = itemParamItemMapper
				.selectByExampleWithBLOBs(example);
		if (list != null && list.size() > 0) {
			TbItemParamItem paramItem = list.get(0);
			try {
				// 把商品信息写入缓存
				jedisClient.set(REDIS_ITEM_KEY + ":" + itemId + ":param",
						JsonUtils.objectToJson(paramItem));
				// 设置key的有效期
				jedisClient.expire(REDIS_ITEM_KEY + ":" + itemId + ":param",
						REDIS_ITEM_EXPIRE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return TaotaoResult.ok(paramItem);
		}
		return TaotaoResult.build(400, "无此商品规格");
	}

}
