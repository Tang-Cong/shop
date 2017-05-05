package org.shop.portal.service;

import org.shop.portal.pojo.SearchResult;

public interface SearchService {

	SearchResult search(String queryString, int page, int rows);
}
