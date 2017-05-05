package org.shop.portal.service;

import org.shop.manager.pojo.TbUser;

public interface UserService {

	TbUser getUserByToken(String token);
}
