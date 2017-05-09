# shop

>前言：基于ssm分布式开发实现的电商项目(聚合工程)

注：本项目为开源项目，不能用于商业应用，仅供学习。

### 使用工具：

	maven（构建项目），svn(版本控制工具)，myeclipse(集成开发环境）,nginx(反向代理)，
	FastDFS	(图片服务器),tomcat(web服务器)，zookeeper(集群管理)，mysql(数据库)
	Junit(测试)

### 技术栈：

	spring,springmvc,mybatis(框架)
	solr(搜索服务)，redis(缓存)，easyUI(后台系统页面)

### 数据库设计

	tb_user用户表(id,username,password,phone,email,created,updated)
	tb_item商品表(id,title,sell_point,price,num,barcode,image,cid,status,created,updated)
	tb_cat商品分类表（id,parent_id,name,status,sort_order,is_parent,created,updated）
	tb_item_desc商品描述表（item_id,item_desc,created,updated）
	tb_item_param商品规格参数表（id,item_cat_id,param_data,created,updated）
	tb_item_param商品规格参数模板表（id,item_id,param_data,created,updated）
	tb_order订单表（payment,payment_type,post_fee,status,create_time,update_time,payment_time,consign_time,end_time,close_time,shipping_name,shipping_code,user_id,buyer_message,buyer_nick,buyer_rate）
	tb_order订单商品表(id,item_id,order_id,num,title,price,total_fee,pic_path)
	tb_order_shipping订单物流表（order_id,receiver_name,receiver_phone,receiver_mobile,receiver_state,receiver_city,receiver_district,receiver_address,receiver_zip,created,updated）
	tb_content_category商品目录分类表(id,parent_id,name,status,sort_order,is_parent,created,updated)
	tb_content商品目录表（id,category_id,title,sub_title,title_desc,url,pic,pic2,content,created,updated）

## 分布式系统

### 商品后台管理系统 
### shop-manager（管理后台）

	商品的添加功能:
	1.商品类目选择-easyui异步tree控件的使用
	2.图片上传（fastdfs+nginx）
	3.富文本编辑器使用KindEditor
	4.分页使用PageHelper插件，插件是基于mybatis的拦截器接口实现的
	
	商品的展示功能：
	1.分页插件的使用PageHelper。
	2.easyUIDataGrid的使用

### 前台系统

### shop-rest（发布服务）

### shop-search（搜索服务）

* 使用solr实现搜索，内容列表使用redis缓存，使用zookeeper管理集群
### shop-sso (单点登录系统)

SSO英文全称Single Sign On，单点登录。SSO是在多个应用系统中，
用户只需要登录一次就可以访问所有相互信任的应用系统。它包括
可以将这次主要的登录映射到其他应用中用于同一个用户的登录的机制。
它是目前比较流行的企业业务整合的解决方案之一。

	用户登录：
	1、接收用户名和密码
	2、校验用户名密码
	3、生成token，可以使用UUID
	4、把用户信息写入redis，key就是token
	5、把token写入cookie。
	6、返回登录成功需要把token返回给客户端。	

	Session共享的问题：
	1、tomcat做集群配置session复制。如果集群中节点很多，会形成网络风暴。推荐节点数量不要超过5个。
	2、分布式架构。拆分成多个子系统。（本项目使用）
	
### shop-order（订单系统）

* 当用户提交订单时此时必须要求用户登录，可以使用拦截器来实现。
 
拦截器的处理流程：

	1. 拦截请求url
	2. 从cookie中取token
	3. 如果没有toke跳转到登录页面。
	4. 取到token，需要调用sso系统的服务查询用户信息。
	5. 如果用户session已经过期，跳转到登录页面
	6. 如果没有过期，放行。

### 代码：

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 在Handler执行之前处理
		// 判断用户是否登录
		// 从cookie中取token
		String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
		// 根据token换取用户信息，调用sso系统的接口。
		TbUser user = userService.getUserByToken(token);
		// 取不到用户信息
		if (null == user) {
			// 跳转到登录页面，把用户请求的url作为参数传递给登录页面。
			response.sendRedirect(userService.SSO_DOMAIN_BASE_USRL
					+ userService.SSO_PAGE_LOGIN + "?redirect="
					+ request.getRequestURL());
			// 返回false
			return false;
		}
		// 取到用户信息，放行
		// 把用户信息放入Request
		request.setAttribute("user", user);
		// 返回值决定handler是否执行。true：执行，false：不执行。
		return true;
	}
	
### shop-portal (门户系统）

	购物车：
		1、添加购物车不需要用户登录。购物车的数据应该放到cookie中。
		2、当向购物车添加同一款商品时，购物车中商品的数量增加。
		3、购物车中可以删除商品
		4、购物车中可以修改商品数量。商品的总价需要重新计算。
		5、点击“结算”按钮要求用户登录。

	购物车问题：
		1、未登录的情况下，可以把购物车写入cookie。
		2、已经登录的情况下，需要把购物车写入redis
		3、登录时判断cookie中有购物车商品，应该吧cookie中的购物车商品列表转移到redis中。
			Key:用户id
			Value：购物车商品列表
		4、如果想redis中转移商品时，redis的购物车中已经有商品，此时，需要把商品合并。如果是同一款商品数量叠加，如果新商品就合并商品。
	跨域问题：
		使用jsonp返回商品目录（ajax方式动态加载）
### 代码：

	/**
	 * 购物车Service
	 */
	@Service
	public class CartServiceImpl implements CartService {

	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${ITME_INFO_URL}")
	private String ITME_INFO_URL;
	@Value("${REDIS_CART_SESSION_KEY}")
	private String REDIS_CART_SESSION_KEY;
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private UserServiceImpl userService;

	/**
	 * 添加购物车商品
	 */
	@Override
	public TaotaoResult addCartItem(long itemId, int num,
			HttpServletRequest request, HttpServletResponse response) {

		// 取商品信息
		CartItem cartItem = null;
		// 取购物车商品列表
		List<CartItem> itemList = getCartItemList(request, response);
		// 判断购物车商品列表中是否存在此商品
		for (CartItem cItem : itemList) {
			// 如果存在此商品longValue()
			if (cItem.getId() == itemId) {
				// 增加商品数量
				cItem.setNum(cItem.getNum() + num);
				cartItem = cItem;
				break;
			}
		}
		if (cartItem == null) {
			cartItem = new CartItem();
			// 根据商品id查询商品基本信息。
			String json = HttpClientUtil.doGet(REST_BASE_URL + ITME_INFO_URL
					+ itemId);
			// 把json转换成java对象
			TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json,
					TbItem.class);
			if (taotaoResult.getStatus() == 200) {
				TbItem item = (TbItem) taotaoResult.getData();
				cartItem.setId(item.getId());
				cartItem.setTitle(item.getTitle());
				cartItem.setImage(item.getImage() == null ? "" : item
						.getImage().split(",")[0]);
				cartItem.setNum(num);
				cartItem.setPrice(item.getPrice());
			}
			// 添加到购物车列表
			itemList.add(cartItem);
		}

		if (login(request)) {// 登录了
			// 将修改写入redis
			String tokenJson = CookieUtils.getCookieValue(request, "TT_TOKEN",
					true);
			TbUser tbUser = userService.getUserByToken(tokenJson);
			jedisClient.set(tbUser.getId().toString(),
					JsonUtils.objectToJson(itemList));
		} else {// 未登录
				// 把购物车列表写入cookie
			CookieUtils.setCookie(request, response, "TT_CART",
					JsonUtils.objectToJson(itemList), true);
		}
		return TaotaoResult.ok();
	}

	/**
	 * 删除购物车商品
	 */
	@Override
	public TaotaoResult deleteCartItem(long itemId, HttpServletRequest request,
			HttpServletResponse response) {
		// 从cookie中取购物车商品列表
		List<CartItem> itemList = getCartItemList(request, response);
		// 从列表中找到此商品
		for (CartItem cartItem : itemList) {
			if (cartItem.getId() == itemId) {
				itemList.remove(cartItem);
				break;
			}

		}
		if (login(request)) {// 登录了
			// 将修改写入redis
			String tokenJson = CookieUtils.getCookieValue(request, "TT_TOKEN",
					true);
			TbUser tbUser = userService.getUserByToken(tokenJson);
			jedisClient.set(tbUser.getId().toString(),
					JsonUtils.objectToJson(itemList));
		} else {// 未登录
				// 把购物车列表写入cookie
			CookieUtils.setCookie(request, response, "TT_CART",
					JsonUtils.objectToJson(itemList), true);
		}

		return TaotaoResult.ok();
	}

	/**
	 * 从cookie中取商品列表
	 */
	private List<CartItem> getCartItemList(HttpServletRequest request) {
		// 从cookie中取商品列表
		String cartJson = CookieUtils.getCookieValue(request, "TT_CART", true);
		if (cartJson == null || "".equals(cartJson)) {
			return new ArrayList<>();
		}
		// 把json转换成商品列表
		try {
			List<CartItem> list = JsonUtils
					.jsonToList(cartJson, CartItem.class);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public List<CartItem> getCartItemList(HttpServletRequest request,
			HttpServletResponse response) {
		List<CartItem> itemList = getCartItemList(request);
		// 登录了就从redis中取数据
		// 未登录就从cookie中取数据
		// 判断是否登录
		if (login(request)) {// 登录了
			String tokenJson = CookieUtils.getCookieValue(request, "TT_TOKEN",
					true);
			TbUser tbUser = userService.getUserByToken(tokenJson);
			String json = jedisClient.get(tbUser.getId().toString());
			if (StringUtils.isBlank(json) || "[]".equals(json)) {// redis为空
				if (itemList.size() > 0) {// cookie不为空
					return sync(itemList, null, request, response);
				} else {
					return itemList;
				}
			} else {// redis不为空
				List<CartItem> list = JsonUtils
						.jsonToList(json, CartItem.class);
				if (itemList.size() > 0) {// cookie不为空
					return sync(itemList, list, request, response);
				} else {
					return sync(null, list, request, response);
				}
			}
		} else {// 未登录
			return itemList;
		}
	}

	@Override
	public Boolean login(HttpServletRequest request) {
		String tokenJson = CookieUtils
				.getCookieValue(request, "TT_TOKEN", true);
		if (tokenJson == null || "".equals(tokenJson)) {
			return false;
		}
		return true;
	}

	@Override
	public List<CartItem> sync(List<CartItem> cookie, List<CartItem> redis,
			HttpServletRequest request, HttpServletResponse response) {
		String tokenJson = CookieUtils
				.getCookieValue(request, "TT_TOKEN", true);
		TbUser tbUser = userService.getUserByToken(tokenJson);
		String json = jedisClient.get(tbUser.getId().toString());
		// 1.redis为空,cookie不为空sync(cookie,null)
		if (redis == null || "".equals(redis) || "[]".equals(redis)) {
			jedisClient.set(tbUser.getId().toString(),
					JsonUtils.objectToJson(cookie));
			List<CartItem> list = JsonUtils.jsonToList(json, CartItem.class);
			CookieUtils.deleteCookie(request, response, "TT_CART");
			return list;
		}

		// 2.redis不为空，cookie为空sync(null, redis)
		if (cookie == null || "".equals(cookie)) {
			return redis;
		}

		// 3.redis，cookie都不为空sync(cookie, redis)
		// 判断购物车商品列表中是否存在此商品
		for (CartItem cItem : cookie) {
			for (CartItem cartItem2 : redis) {// redis
				// 如果存在此商品longValue()
				if (cItem.getId() == cartItem2.getId()) {
					// 增加商品数量
					cItem.setNum(cItem.getNum() + cartItem2.getNum());
					redis.remove(cartItem2);
					redis.add(cItem);
					break;
				} else {
					redis.add(cItem);
				}
			}
		}
		jedisClient.set(tbUser.getId().toString(),
				JsonUtils.objectToJson(redis));
		CookieUtils.deleteCookie(request, response, "TT_CART");
		String json2 = jedisClient.get(tbUser.getId().toString());
		List<CartItem> listItem = JsonUtils.jsonToList(json2, CartItem.class);
		return listItem;
		}
	}
老铁们凑合这看吧😁，觉得还行的可以点个star，**你的star是我继续开源创作的动力**，谢谢！！！
