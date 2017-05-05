package org.shop.portal.pojo;

import org.shop.manager.pojo.TbItem;

public class ItemInfo extends TbItem {

	public String[] getImages() {
		String image = getImage();
		if (image != null) {
			String[] images = image.split(",");
			return images;
		}
		return null;
	}
}
