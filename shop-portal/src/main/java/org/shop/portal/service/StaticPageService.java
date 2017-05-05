package org.shop.portal.service;

import org.shop.common.pojo.TaotaoResult;

public interface StaticPageService {

	TaotaoResult genItemHtml(Long itemId) throws Exception;
}
