package org.shop.search.service;

import org.shop.search.pojo.SearchResult;

public interface SearchService {

	SearchResult search(String queryString, int page, int rows)
			throws Exception;
}
