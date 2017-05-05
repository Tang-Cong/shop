package org.shop.rest.service;

import org.shop.common.pojo.TaotaoResult;

public interface RedisService {

	TaotaoResult syncContent(long contentCid);
}
