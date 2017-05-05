package org.shop.manager.service.impl;

import java.util.Date;
import java.util.List;

import org.shop.common.pojo.EUDataGridResult;
import org.shop.common.pojo.TaotaoResult;
import org.shop.common.utils.HttpClientUtil;
import org.shop.manager.dao.TbContentMapper;
import org.shop.manager.pojo.TbContent;
import org.shop.manager.pojo.TbContentExample;
import org.shop.manager.pojo.TbContentExample.Criteria;
import org.shop.manager.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * 内容管理
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${REST_CONTENT_SYNC_URL}")
	private String REST_CONTENT_SYNC_URL;

	@Override
	public TaotaoResult insertContent(TbContent content) {
		// 补全pojo内容
		content.setCreated(new Date());
		content.setUpdated(new Date());
		contentMapper.insert(content);

		// 添加缓存同步逻辑
		try {
			HttpClientUtil.doGet(REST_BASE_URL + REST_CONTENT_SYNC_URL
					+ content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return TaotaoResult.ok();
	}

	@Override
	public EUDataGridResult getItemList(int page, int rows) {
		// 查询商品列表
		TbContentExample example = new TbContentExample();
		// 分页处理
		PageHelper.startPage(page, rows);
		List<TbContent> list = contentMapper.selectByExample(example);
		// 创建一个返回值对象
		EUDataGridResult result = new EUDataGridResult();
		result.setRows(list);
		// 取记录总条数
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

	@Override
	public TaotaoResult deleteItem(String id) {
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		long Id = Long.valueOf(id).longValue();
		criteria.andIdEqualTo(Id);
		int result = contentMapper.deleteByExample(example);
		if (result == 1) {
			return TaotaoResult.ok();
		}
		return null;
	}

}
