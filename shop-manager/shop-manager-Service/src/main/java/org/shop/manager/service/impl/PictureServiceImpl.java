package org.shop.manager.service.impl;

import org.shop.common.pojo.PictureResult;
import org.shop.common.utils.FastDFSClient;
import org.shop.manager.service.PictureService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片上传服务
 */
@Service
public class PictureServiceImpl implements PictureService {

	@Value("${IMAGE_SERVER_BASE_URL}")
	private String IMAGE_SERVER_BASE_URL;

	@Override
	public PictureResult uploadPicture(MultipartFile uploadFile) {
		PictureResult result = new PictureResult();
		// 判断图片是否为空
		if (uploadFile.isEmpty()) {
			result.setError(1);
			result.setMessage("图片为空");
			return result;
		}
		// 上传到图片服务器
		try {
			// 取图片扩展名
			String originalFilename = uploadFile.getOriginalFilename();
			// 取扩展名不要“.”
			String extName = originalFilename.substring(originalFilename
					.lastIndexOf(".") + 1);
			FastDFSClient client = new FastDFSClient(
					"C:\\Users\\asus\\Workspaces\\MyEclipse 2015\\shop-manager\\shop-manager-web\\src\\main\\resources\\client.conf");
			String url = client.uploadFile(uploadFile.getBytes(), extName);
			// 把url响应给客户端
			url = IMAGE_SERVER_BASE_URL + url;
			result.setError(0);
			result.setUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
