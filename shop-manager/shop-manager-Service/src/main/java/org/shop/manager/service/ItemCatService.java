package org.shop.manager.service;

import java.util.List;

import org.shop.common.pojo.EUTreeNode;

public interface ItemCatService {

	List<EUTreeNode> getCatList(long parentId);
}
