package org.shop.web.controller;

import org.shop.common.pojo.PictureResult;
import org.shop.common.utils.JsonUtils;
import org.shop.manager.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传图片处理
 */
@Controller
public class PictureController {

	@Autowired
	private PictureService pictureService;

	@RequestMapping("/pic/upload")
	@ResponseBody
	public String uploadFile(MultipartFile uploadFile) {
		PictureResult result = pictureService.uploadPicture(uploadFile);
		// return result;
		// 为了保证功能的兼容性，需要把Result转换成json格式的字符串。
		String json = JsonUtils.objectToJson(result);
		return json;
	}
}
