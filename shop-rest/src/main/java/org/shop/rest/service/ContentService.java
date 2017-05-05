package org.shop.rest.service;

import java.util.List;

import org.shop.manager.pojo.TbContent;

public interface ContentService {

	List<TbContent> getContentList(long contentCid);
}
