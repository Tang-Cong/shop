package org.shop.manager.service;

import java.util.List;

import org.shop.common.pojo.EUTreeNode;
import org.shop.common.pojo.TaotaoResult;

public interface ContentCategoryService {

	List<EUTreeNode> getCategoryList(long parentId);

	TaotaoResult insertContentCategory(long parentId, String name);

	TaotaoResult updateContentCategory(long id, String name);

	TaotaoResult deleteContentCategory(long id);
}
