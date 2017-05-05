package org.shop.portal.service.impl;

import org.shop.common.pojo.TaotaoResult;
import org.shop.common.utils.HttpClientUtil;
import org.shop.manager.pojo.TbUser;
import org.shop.portal.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 用户管理Service
 */
@Service
public class UserServiceImpl implements UserService {

	@Value("${SSO_BASE_URL}")
	public String SSO_BASE_URL;
	@Value("${SSO_DOMAIN_BASE_USRL}")
	public String SSO_DOMAIN_BASE_USRL;
	@Value("${SSO_USER_TOKEN}")
	private String SSO_USER_TOKEN;
	@Value("${SSO_PAGE_LOGIN}")
	public String SSO_PAGE_LOGIN;

	@Override
	public TbUser getUserByToken(String token) {
		try {
			// 调用sso系统的服务，根据token取用户信息
			String json = HttpClientUtil.doGet(SSO_DOMAIN_BASE_USRL
					+ SSO_USER_TOKEN + token);
			// 把json转换成TaotaoREsult
			TaotaoResult result = TaotaoResult.formatToPojo(json, TbUser.class);
			if (result.getStatus() == 200) {
				TbUser user = (TbUser) result.getData();
				return user;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
