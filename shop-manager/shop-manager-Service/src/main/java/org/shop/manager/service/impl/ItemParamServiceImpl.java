package org.shop.manager.service.impl;

import java.util.Date;
import java.util.List;

import org.shop.common.pojo.EUDataGridResult;
import org.shop.common.pojo.TaotaoResult;
import org.shop.manager.dao.TbItemParamMapper;
import org.shop.manager.pojo.TbItemParam;
import org.shop.manager.pojo.TbItemParamExample;
import org.shop.manager.pojo.TbItemParamExample.Criteria;
import org.shop.manager.service.ItemParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * 商品规格参数模板管理
 */
@Service
public class ItemParamServiceImpl implements ItemParamService {

	@Autowired
	private TbItemParamMapper itemParamMapper;

	@Override
	public TaotaoResult getItemParamByCid(long cid) {
		TbItemParamExample example = new TbItemParamExample();
		Criteria criteria = example.createCriteria();
		criteria.andItemCatIdEqualTo(cid);
		List<TbItemParam> list = itemParamMapper
				.selectByExampleWithBLOBs(example);
		// 判断是否查询到结果
		if (list != null && list.size() > 0) {
			return TaotaoResult.ok(list.get(0));
		}

		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult insertItemParam(TbItemParam itemParam) {
		// 补全pojo
		itemParam.setCreated(new Date());
		itemParam.setUpdated(new Date());
		// 插入到规格参数模板表
		itemParamMapper.insert(itemParam);
		return TaotaoResult.ok();
	}

	@Override
	public EUDataGridResult getItemParamList(int page, int rows) {
		// 查询规格参数模板
		TbItemParamExample example = new TbItemParamExample(); // 分页处理
		PageHelper.startPage(page, rows);
		List<TbItemParam> list = itemParamMapper
				.selectByExampleWithBLOBs(example); // 创建一个返回值对象
		EUDataGridResult result = new EUDataGridResult();
		result.setRows(list); // 取记录总条数
		PageInfo<TbItemParam> pageInfo = new PageInfo<>(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	@Override
	public TaotaoResult deleteItemParam(String id) {
		TbItemParamExample example = new TbItemParamExample();
		Criteria criteria = example.createCriteria();
		long Id = Long.valueOf(id).longValue();
		criteria.andIdEqualTo(Id);
		int result = itemParamMapper.deleteByExample(example);
		if (result == 1) {
			return TaotaoResult.ok();
		}
		return null;
	}

}
