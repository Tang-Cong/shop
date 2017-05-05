package org.shop.search.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.shop.search.pojo.SearchResult;

public interface SearchDao {

	SearchResult search(SolrQuery query) throws Exception;
}
