package org.shop.manager.service;

import org.shop.common.pojo.PictureResult;
import org.springframework.web.multipart.MultipartFile;

public interface PictureService {

	PictureResult uploadPicture(MultipartFile uploadFile);
}
