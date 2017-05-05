package org.shop.manager.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.shop.common.pojo.EUTreeNode;
import org.shop.common.pojo.TaotaoResult;
import org.shop.manager.dao.TbContentCategoryMapper;
import org.shop.manager.pojo.TbContentCategory;
import org.shop.manager.pojo.TbContentCategoryExample;
import org.shop.manager.pojo.TbContentCategoryExample.Criteria;
import org.shop.manager.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 内容分类管理
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;

	@Override
	public List<EUTreeNode> getCategoryList(long parentId) {
		// 根据parentid查询节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		// 执行查询
		List<TbContentCategory> list = contentCategoryMapper
				.selectByExample(example);
		List<EUTreeNode> resultList = new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			// 创建一个节点
			EUTreeNode node = new EUTreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent() ? "closed" : "open");

			resultList.add(node);
		}
		return resultList;
	}

	@Override
	public TaotaoResult insertContentCategory(long parentId, String name) {

		// 创建一个pojo
		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setName(name);
		contentCategory.setIsParent(false);
		// '状态。可选值:1(正常),2(删除)',
		contentCategory.setStatus(1);
		contentCategory.setParentId(parentId);
		contentCategory.setSortOrder(1);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		// 添加记录
		contentCategoryMapper.insert(contentCategory);
		// 查看父节点的isParent列是否为true，如果不是true改成true
		TbContentCategory parentCat = contentCategoryMapper
				.selectByPrimaryKey(parentId);
		// 判断是否为true
		if (!parentCat.getIsParent()) {
			parentCat.setIsParent(true);
			// 更新父节点
			contentCategoryMapper.updateByPrimaryKey(parentCat);
		}
		// 返回结果
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	public TaotaoResult updateContentCategory(long id, String name) {
		// 创建一个pojo
		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setName(name);
		contentCategory.setId(id);
		// 添加记录
		int result = contentCategoryMapper
				.updateByPrimaryKeySelective(contentCategory);
		if (result == 1) {
			return TaotaoResult.ok();
		}
		return null;
	}

	@Override
	public TaotaoResult deleteContentCategory(long id) {

		TbContentCategory tcc = contentCategoryMapper.selectByPrimaryKey(id);
		boolean isParent = tcc.getIsParent();
		// 是父节点，删除下面的所有子节点
		if (isParent) {
			TbContentCategoryExample example = new TbContentCategoryExample();
			Criteria criteria = example.createCriteria();
			criteria.andParentIdEqualTo(id);
			contentCategoryMapper.deleteByExample(example);
			int result = contentCategoryMapper.deleteByPrimaryKey(id);
			if (result == 1) {
				return TaotaoResult.ok();
			}
			return null;

		} else {
			// 不是父节点，删除当前节点
			int result = contentCategoryMapper.deleteByPrimaryKey(id);
			if (result == 1) {
				return TaotaoResult.ok();
			}
			return null;
		}

	}
}
