package org.shop.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.shop.common.pojo.TaotaoResult;
import org.shop.common.utils.CookieUtils;
import org.shop.common.utils.JsonUtils;
import org.shop.manager.dao.TbUserMapper;
import org.shop.manager.pojo.TbUser;
import org.shop.manager.pojo.TbUserExample;
import org.shop.manager.pojo.TbUserExample.Criteria;
import org.shop.sso.dao.JedisClient;
import org.shop.sso.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户管理Service
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	@Autowired
	private JedisClient jedisClient;

	@Value("${REDIS_USER_SESSION_KEY}")
	private String REDIS_USER_SESSION_KEY;
	@Value("${SSO_SESSION_EXPIRE}")
	private Integer SSO_SESSION_EXPIRE;

	@Override
	public TaotaoResult checkData(String content, Integer type) {
		// 创建查询条件
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		// 对数据进行校验：1、2、3分别代表username、phone、email
		// 用户名校验
		if (1 == type) {
			criteria.andUsernameEqualTo(content);
			// 电话校验
		} else if (2 == type) {
			criteria.andPhoneEqualTo(content);
			// email校验
		} else {
			criteria.andEmailEqualTo(content);
		}
		// 执行查询
		List<TbUser> list = userMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			return TaotaoResult.ok(true);
		}
		return TaotaoResult.ok(false);
	}

	@Override
	public TaotaoResult createUser(TbUser user) {
		if (StringUtils.isBlank(user.getUsername())
				|| StringUtils.isBlank(user.getPassword())) {
			return TaotaoResult.build(400, "用户名或密码不能为空");
		}
		// 校验数据是否重复
		// 校验用户名
		TaotaoResult result = checkData(user.getUsername(), 1);
		if (!(boolean) result.getData()) {
			return TaotaoResult.build(400, "用户名重复");
		}
		// 校验手机号
		if (user.getPhone() != null) {
			result = checkData(user.getPhone(), 2);
			if (!(boolean) result.getData()) {
				return TaotaoResult.build(400, "手机号重复");
			}
		}
		// 校验邮箱
		if (user.getEmail() != null) {
			result = checkData(user.getEmail(), 3);
			if (!(boolean) result.getData()) {
				return TaotaoResult.build(400, "邮箱重复");
			}
		}

		user.setUpdated(new Date());
		user.setCreated(new Date());
		// md5加密
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword()
				.getBytes()));
		userMapper.insert(user);
		return TaotaoResult.ok();
	}

	/**
	 * 用户登录
	 * <p>
	 * Title: userLogin
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @see com.taotao.sso.service.UserService#userLogin(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public TaotaoResult userLogin(String username, String password,
			HttpServletRequest request, HttpServletResponse response) {

		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = userMapper.selectByExample(example);
		// 如果没有此用户名
		if (null == list || list.size() == 0) {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		// 比对密码
		if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(
				user.getPassword())) {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		// 生成token
		String token = UUID.randomUUID().toString();
		// 保存用户之前，把用户对象中的密码清空。
		user.setPassword(null);
		// 把用户信息写入redis
		jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token,
				JsonUtils.objectToJson(user));
		// 设置session的过期时间
		jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token,
				SSO_SESSION_EXPIRE);

		// 添加写cookie的逻辑，cookie的有效期是关闭浏览器就失效。
		CookieUtils.setCookie(request, response, "TT_TOKEN", token);

		// 返回token
		return TaotaoResult.ok(token);
	}

	@Override
	public TaotaoResult getUserByToken(String token) {

		// 根据token从redis中查询用户信息
		String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token);
		// 判断是否为空
		if (StringUtils.isBlank(json)) {
			return TaotaoResult.build(400, "此session已经过期，请重新登录");
		}
		// 更新过期时间
		jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token,
				SSO_SESSION_EXPIRE);
		// 返回用户信息
		return TaotaoResult.ok(JsonUtils.jsonToPojo(json, TbUser.class));
	}

	@Override
	public TaotaoResult deleteToken(String token, HttpServletRequest request,
			HttpServletResponse response) {

		// 根据token从redis中查询用户信息
		String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token);
		// 判断是否为空
		if (StringUtils.isBlank(json)) {
			return TaotaoResult.build(400, "此session已经过期，请重新登录");
		}
		jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, 0);
		CookieUtils.deleteCookie(request, response, "TT_TOKEN");
		// 返回用户信息
		return TaotaoResult.ok();
	}

}
