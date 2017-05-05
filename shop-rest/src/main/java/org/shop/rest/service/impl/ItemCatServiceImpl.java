package org.shop.rest.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.shop.common.utils.JsonUtils;
import org.shop.manager.dao.TbItemCatMapper;
import org.shop.manager.pojo.TbItemCat;
import org.shop.manager.pojo.TbItemCatExample;
import org.shop.manager.pojo.TbItemCatExample.Criteria;
import org.shop.rest.dao.JedisClient;
import org.shop.rest.pojo.CatNode;
import org.shop.rest.pojo.CatResult;
import org.shop.rest.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 商品分类服务
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${INDEX_ITEMCAT_REDIS_KEY}")
	private String INDEX_ITEMCAT_REDIS_KEY;

	@Override
	public CatResult getItemCatList() {

		CatResult catResult = new CatResult();
		// 查询分类列表
		catResult.setData(getCatList(0));
		return catResult;
	}

	/**
	 * 查询分类列表
	 * <p>
	 * Title: getCatList
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param parentId
	 * @return
	 */
	private List<?> getCatList(long parentId) {

		// 从缓存中取内容
		try {
			String result = jedisClient.hget(INDEX_ITEMCAT_REDIS_KEY, parentId
					+ "");
			if (!StringUtils.isBlank(result)) { // 把字符串转换成list
				List<CatNode> resultList = JsonUtils.jsonToList(result,
						CatNode.class);
				return resultList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 创建查询条件
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		// 执行查询
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		// 返回值list
		List resultList = new ArrayList<>();

		// 向缓存中添加内容
		try {
			// 向list中添加节点
			int count = 0;
			for (TbItemCat tbItemCat : list) {
				// 判断是否为父节点
				if (tbItemCat.getIsParent()) {
					CatNode catNode = new CatNode();
					if (parentId == 0) {
						catNode.setName("<a href='/products/"
								+ tbItemCat.getId() + ".html'>"
								+ tbItemCat.getName() + "</a>");
					} else {
						catNode.setName(tbItemCat.getName());
					}
					catNode.setUrl("/products/" + tbItemCat.getId() + ".html");
					catNode.setItem(getCatList(tbItemCat.getId()));

					resultList.add(catNode);
					count++;
					// 第一层只取14条记录
					if (parentId == 0 && count >= 14) {
						break;
					}
					// 如果是叶子节点
				} else {
					resultList.add("/products/" + tbItemCat.getId() + ".html|"
							+ tbItemCat.getName());
				}
			}
			// 把list转换成字符串
			String cacheString = JsonUtils.objectToJson(resultList);
			jedisClient.hset(INDEX_ITEMCAT_REDIS_KEY, parentId + "",
					cacheString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultList;
	}

}
