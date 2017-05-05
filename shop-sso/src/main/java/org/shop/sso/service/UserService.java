package org.shop.sso.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shop.common.pojo.TaotaoResult;
import org.shop.manager.pojo.TbUser;

public interface UserService {

	TaotaoResult checkData(String content, Integer type);

	TaotaoResult createUser(TbUser user);

	TaotaoResult userLogin(String username, String password,
			HttpServletRequest request, HttpServletResponse response);

	TaotaoResult getUserByToken(String token);

	TaotaoResult deleteToken(String token, HttpServletRequest request,
			HttpServletResponse response);
}
