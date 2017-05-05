package org.shop.search;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrCloudTest {

	@Test
	public void testAddDocument() throws Exception {
		// 创建一个和solr集群的连接
		// 参数就是zookeeper的地址列表，使用逗号分隔
		String zkHost = "192.168.13.129:2181,192.168.13.129:2182,192.168.13.129:2183";
		CloudSolrServer solrServer = new CloudSolrServer(zkHost);
		// 设置默认的collection
		solrServer.setDefaultCollection("collection2");
		// 创建一个文档对象
		SolrInputDocument document = new SolrInputDocument();
		// 向文档中添加域
		document.addField("id", "test001");
		document.addField("item_title", "测试商品");
		// 把文档添加到索引库
		solrServer.add(document);
		// 提交
		solrServer.commit();
	}

	@Test
	public void deleteDocument() throws SolrServerException, IOException {
		// 创建一个和solr集群的连接
		// 参数就是zookeeper的地址列表，使用逗号分隔
		String zkHost = "192.168.13.129:2181,192.168.13.129:2182,192.168.13.129:2183";
		CloudSolrServer solrServer = new CloudSolrServer(zkHost);
		// 设置默认的collection
		solrServer.setDefaultCollection("collection2");

		solrServer.deleteByQuery("*:*");
		solrServer.commit();
	}
}
