package org.shop.manager.service.impl;

import java.util.Date;
import java.util.List;

import org.shop.common.pojo.EUDataGridResult;
import org.shop.common.pojo.TaotaoResult;
import org.shop.common.utils.IDUtils;
import org.shop.manager.dao.TbItemDescMapper;
import org.shop.manager.dao.TbItemMapper;
import org.shop.manager.dao.TbItemParamItemMapper;
import org.shop.manager.pojo.TbItem;
import org.shop.manager.pojo.TbItemDesc;
import org.shop.manager.pojo.TbItemDescExample;
import org.shop.manager.pojo.TbItemDescExample.Criteria;
import org.shop.manager.pojo.TbItemExample;
import org.shop.manager.pojo.TbItemParamItem;
import org.shop.manager.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * 商品管理Service
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbItemDescMapper itemDescMapper;

	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;

	@Override
	public TbItem getItemById(long itemId) {

		// TbItem item = itemMapper.selectByPrimaryKey(itemId);
		// 添加查询条件
		TbItemExample example = new TbItemExample();
		org.shop.manager.pojo.TbItemExample.Criteria criteria = example
				.createCriteria();
		criteria.andIdEqualTo(itemId);
		List<TbItem> list = itemMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			TbItem item = list.get(0);
			return item;
		}
		return null;
	}

	/**
	 * 商品列表查询
	 * <p>
	 * Title: getItemList
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param page
	 * @param rows
	 * @return
	 * @see org.shop.manager.service.ItemService#getItemList(long, long)
	 */
	@Override
	public EUDataGridResult getItemList(int page, int rows) {
		// 查询商品列表
		TbItemExample example = new TbItemExample();
		// 分页处理
		PageHelper.startPage(page, rows);
		List<TbItem> list = itemMapper.selectByExample(example);
		// 创建一个返回值对象
		EUDataGridResult result = new EUDataGridResult();
		result.setRows(list);
		// 取记录总条数
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	@Override
	public TaotaoResult createItem(TbItem item, String desc, String itemParam)
			throws Exception {
		// item补全
		// 生成商品ID
		Long itemId = IDUtils.genItemId();
		item.setId(itemId);
		// '商品状态，1-正常，2-下架，3-删除',
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		// 插入到数据库
		itemMapper.insert(item);
		// 添加商品描述信息
		TaotaoResult result = insertItemDesc(itemId, desc);
		if (result.getStatus() != 200) {
			throw new Exception();
		}
		// 添加规格参数
		result = insertItemParamItem(itemId, itemParam);
		if (result.getStatus() != 200) {
			throw new Exception();
		}
		return TaotaoResult.ok();
	}

	/**
	 * 添加商品描述
	 * <p>
	 * Title: insertItemDesc
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param desc
	 */
	private TaotaoResult insertItemDesc(Long itemId, String desc) {
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		itemDescMapper.insert(itemDesc);
		return TaotaoResult.ok();
	}

	public TaotaoResult updateItemDesc(Long itemId, String desc) {
		TbItemDescExample example = new TbItemDescExample();
		TbItemDesc itemDesc = new TbItemDesc();
		Criteria criteria = example.createCriteria();
		criteria.andItemIdEqualTo(itemId);
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		itemDescMapper.updateByExampleWithBLOBs(itemDesc, example);
		return TaotaoResult.ok();
	}

	/**
	 * 添加规格参数
	 * <p>
	 * Title: insertItemParamItem
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param itemId
	 * @param itemParam
	 * @return
	 */
	private TaotaoResult insertItemParamItem(Long itemId, String itemParam) {
		// 创建一个pojo
		TbItemParamItem itemParamItem = new TbItemParamItem();
		itemParamItem.setItemId(itemId);
		itemParamItem.setParamData(itemParam);
		itemParamItem.setCreated(new Date());
		itemParamItem.setUpdated(new Date());
		// 向表中插入数据
		itemParamItemMapper.insert(itemParamItem);

		return TaotaoResult.ok();

	}

	/*
	 * private TaotaoResult updateItemParamItem(Long itemId, String itemParam,
	 * String itemParamId) { // 创建一个pojo TbItemParamItem itemParamItem = new
	 * TbItemParamItem(); TbItemParamItemExample example = new
	 * TbItemParamItemExample();
	 * org.shop.manager.pojo.TbItemParamItemExample.Criteria criteria = example
	 * .createCriteria(); criteria.andItemIdEqualTo(itemId); boolean flag =
	 * itemParamId.length() > 0; if (flag) { long id = Long.valueOf("0");
	 * itemParamItem.setId(id); itemParamItem.setItemId(itemId);
	 * itemParamItem.setParamData(itemParam); itemParamItem.setCreated(new
	 * Date()); itemParamItem.setUpdated(new Date()); // 向表中插入数据
	 * itemParamItemMapper .updateByExampleWithBLOBs(itemParamItem, example);
	 * return TaotaoResult.ok(); } return null;
	 * 
	 * }
	 */

	@Override
	public TaotaoResult updateItem(TbItem item, String desc, String itemParam)
			throws Exception {
		TbItemExample example = new TbItemExample();
		org.shop.manager.pojo.TbItemExample.Criteria criteria = example
				.createCriteria();
		Long itemId = item.getId();
		criteria.andIdEqualTo(itemId);
		// item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		// 插入到数据库
		itemMapper.updateByExample(item, example);
		// 修改商品描述信息
		TaotaoResult result = updateItemDesc(itemId, desc);
		if (result.getStatus() != 200) {
			throw new Exception();
		}

		/*
		 * if (itemParamId.length() > 0) { // 修改规格参数 result =
		 * updateItemParamItem(itemId, itemParam, itemParamId); if
		 * (result.getStatus() != 200) { throw new Exception(); } }
		 */

		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult deleteItem(String id) {
		TbItemExample example = new TbItemExample();
		org.shop.manager.pojo.TbItemExample.Criteria criteria = example
				.createCriteria();
		long Id = Long.valueOf(id).longValue();
		criteria.andIdEqualTo(Id);
		int result = itemMapper.deleteByExample(example);
		if (result == 1) {
			return TaotaoResult.ok();
		}
		return null;
	}
}
