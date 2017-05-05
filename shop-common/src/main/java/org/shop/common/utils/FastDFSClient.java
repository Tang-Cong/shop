package org.shop.common.utils;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class FastDFSClient {

	private TrackerClient trackerClient = null;
	private TrackerServer trackerServer = null;
	private StorageServer storageServer = null;
	private StorageClient1 storageClient = null;

	public FastDFSClient(String conf) throws Exception {

		if (conf.contains("classpath:")) {
			String url = this.getClass().getResource("/").getPath();
			url = url.substring(1);
			conf = conf.replace("classpath:", url);
		}
		ClientGlobal.init(conf);
		trackerClient = new TrackerClient();
		trackerServer = trackerClient.getConnection();
		storageServer = null;
		storageClient = new StorageClient1(trackerServer, storageServer);
	}

	public String uploadFile(String fileName, String extName,
			NameValuePair[] metas) throws Exception {
		return storageClient.upload_file1(fileName, extName, metas);
	}

	public String uploadFile(String fileName, String extName) throws Exception {
		return storageClient.upload_file1(fileName, extName, null);
	}

	public String uploadFile(String fileName) throws Exception {
		return storageClient.upload_file1(fileName, null, null);
	}

	public String uploadFile(byte[] fileContent, String extName,
			NameValuePair[] metas) throws Exception {
		return storageClient.upload_file1(fileContent, extName, metas);
	}

	public String uploadFile(byte[] fileContent, String extName)
			throws Exception {
		return storageClient.upload_file1(fileContent, extName, null);
	}

	public String uploadFile(byte[] fileContent) throws Exception {
		return storageClient.upload_file1(fileContent, null, null);
	}
}