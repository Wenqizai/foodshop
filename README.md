## 1. 登录与注册

> 常见的用户注册登录流程

<img src="https://i.loli.net/2021/01/03/r39FTQyxzCcIWBY.png" style="zoom:50%;" />

>  本项目采用方案

本次项目采用注册方案为两次校验密码的方式. 大致流程如下: 

```txt
注册 -> 提交注册信息 -> 后端校验信息 -> 设置默认信息并插库 -> 返回user对象, 用于页面显示
```

查询到信息进行一些脱敏之后（设置为null），将信息设置到cookie中，过程使用cookie进行信息交互。

```java
CookieUtils.setCookie(request, response, "user",
        JsonUtils.objectToJson(userResult), true);
```

退出登录时:

1. 需把cookie删除
2. 清空购物车信息
3. 分布式会话中, 清除用户数据。

```java
// 清除用户的相关信息的cookie
CookieUtils.deleteCookie(request, response, "user");
```

## 2. 整合Swagger 2.0

引入全新的Swagger界面（基于Bootstrap），原有UI界面同时保留。

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>swagger-bootstrap-ui</artifactId>
    <version>1.6</version>
</dependency>
```

参看 ：[Swagger官方访问路径](http://localhost:8088/swagger-ui.html)  [全新界面访问路径](http://localhost:8088/doc.html)

## 3. 日志管理

1. 整合`slf4j + log4j`，添加`log4j.properties`。
2. 借助`Spring AOP`用来记录每个service的执行时间。
   - 执行时间>3s，输出日志级别为ERRO。

   - 执行时间>2s，输出日志级别为WARN。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

## 4. 首页显示

### 4.1 轮播图

​	按顺序展示, 包含图片背景色。

### 4.2 商品分类

1.  第一次刷新主页查询大分类, 渲染展示到首页
2.  如果鼠标上移到大分类, 则加载其子分类的内容, 如果已经存在子分类, 则不需要加载(懒加载)
3.  商品推荐展示, 默认显示最新6个商品(懒加载)

实现：自定义`VO`类, 利用`MyBatis`帮忙封装分类。

```java
@Data
public class CategoryVO {
    private Integer id;
    private String name;
    private String type;
    private Integer fatherId;
    /**
     * 三级分类VO List
     */
    private List<SubCategoryVO> subCatList;
}
```

```java
@Data
public class SubCategoryVO {
    private Integer subId;
    private String subName;
    private String subType;
    private Integer subFatherId;
}
```

```xml
<resultMap id="myCategoryVO" type="com.imooc.pojo.vo.CategoryVO">
  <id column="id" property="id"/>
  <result column="name" property="name"/>
  <result column="type" property="type"/>
  <result column="fatherId" property="fatherId"/>
  <collection property="subCatList" ofType="com.imooc.pojo.vo.SubCategoryVO">
    <id column="subId" property="subId"/>
    <result column="subName" property="subName"/>
    <result column="subType" property="subType"/>
    <result column="subFatherId" property="subFatherId"/>
  </collection>
</resultMap>
```

## 5. 商品详情

### 5.1 商品信息展示

包含四部分封装到`ItemInfoVO`：商品信息`item`，商品图片`itemImgList`，商品规格`itemSpecList`，商品参数`itemParams`。

### 5.2 商品评价

1. 分页展示
2. 昵称脱敏，借助工具类`DesensitizationUtil`（实现：利用`StringBuilder`拼接字符串）。
   - 昵称位数 <= 2：保留最后一位，前面脱敏

   - 昵称位数 = 3：前后各保留最后一位，中间脱敏

   - 昵称位数 < 8：前后各保留最后一位，中间脱敏

   - 昵称位数 >= 8：中间脱敏6位

### 5.3 商品搜索

1. 商品名称模糊查询，并按商品名称或销量或价格进行排序。

   `i.item_name like '%${paramsMap.keywords}%'`

2. 根据三级分类id查询

## 6. 购物车

1. 数据存储
   - 未登录状态 : 使用cookie
   - 登录后状态 : 使用Redis
2. 刷新购物车
   - 主要考虑购物车中的数据(主要是商品的价格)可能已经发生变化，需要更新。
3. 删除购物车数据
   - 删除Cookie
   - 删除Redis

## 7. 订单

>  收货地址

1. 查询用户的所有收货地址列表 
2. 新增收货地址
   1. 查询数据库是否有地址，没有则新增的地址设置为默认地址
   2. 新增时需对参数进行校验，即使前端已做校验  。
3. 删除收货地址
4. 修改收货地址
5. 设置默认收货地址
   1. 查找默认地址设置为不默认
   2. 根据地址id修改为默认地址

> 订单

1. 订单处理流程图

![](https://i.loli.net/2021/01/03/VCozDLErUkYtWsi.png)

2. 创建订单
   - 设置订单信息 ->  查询商品信息 -> 设置商品信息 -> 获取购物车中的商品数量 -> 计算商品价格和支付价格 -> 订单保存到数据库 -> 库存减除 -> 设置订单状态
   - ==注意 :== 减库存时商品超卖问题

```sql
update items_spec set stock = stock - #{pendingCounts} where id = #{specId} and stock >= #{pendingCounts}
```

```markdown
// 解决商品超卖问题的思路
1. synchronized 不推荐使用, 集群下无用, 性能低下
2. 锁数据库 : 不推荐, 导致数据库性能低下
3. 单体应用 : sql语句的乐观锁
4. 分布式应用 : 分布式锁 zookeeper redis
```

3. 订单关闭

   - 支付成功后的关闭
   - 未支付超时的订单关闭(定时任务)

   ==使用定时任务关闭超期未支付订单的弊端:== 

   		1. 存在定时的时间差，不能准时关闭订单。（程序不严谨）
   		2. 不支持集群。（不需要每个节点都执行定时任务）
   	     		1. 解决方案：只使用一台计算机节点，单独用来运行所有的定时任务。
   		3. 会对数据库全表搜索，极其影响数据库性能。

   ==定时任务，仅仅适用于小型轻量级项目，传统项目==, 大型分布式项目可使用消息队列来处理。

## 8. 支付 

### 微信支付

本项目因没有注册商户平台，故不具备使用微信支付的资质。因此使用第三方支付中心来完成支付流程。

> 总流程时序图

![](C:\@D\-Development\Study\Codes\java-idea\Learning\Project\foodie-dev-git\图片存放\微信支付.png)

#### 1 创建商单

<img src="https://i.loli.net/2021/01/03/gxaAdFWLIK5jtc1.png" style="zoom:50%;" />

#### 2 获取二维码

> 统一下单(发送到微信支付中心)

1. 时序图

<img src="https://i.loli.net/2021/01/03/rwVpchdBaGWNxuX.png" style="zoom: 33%;" />

2. 请求url地址

https://api.mch.weixin.qq.com/pay/unifiedorder

3. 请求参数(xml)

|      参数名      |       参数       | 是否必须 |    类型     |
| :--------------: | :--------------: | :------: | :---------: |
|    商户订单号    |   out_trade_no   |    是    | String(32)  |
|     商品描述     |       body       |    是    | String(128) |
|     标价金额     |    total_fee     |    是    |     Int     |
|    公众账号ID    |      appid       |    是    | String(32)  |
| 商户号(微信分配) |      mch_id      |    是    | String(32)  |
|     通知地址     |    notify_url    |    是    | String(256) |
|    随机字符串    |    nonce_str     |    是    | String(32)  |
|     交易类型     |    trade_type    |    是    | String(16)  |
|      终端IP      | spbill_create_ip |    是    | String(64)  |
|       签名       |       sign       |    是    | String(32)  |

4. 返回结果(xml)

|      参数       |       参数名       |
| :-------------: | :----------------: |
| ==return_code== |     返回状态码     |
|   return_msg    |      返回信息      |
|      appid      |     公众账号ID     |
|     mch_id      |       商户号       |
|   device_info   |       设备号       |
|    nonce_str    |     随机字符串     |
|      sign       |        签名        |
|   result_code   |      业务结果      |
|    err_code     |      错误代码      |
|  err_code_des   |    错误代码描述    |
|   trade_type    |      交易类型      |
|    prepay_id    | 预支付交易会话标识 |
|  ==code_url==   |     二维码链接     |

#### 3 扫码支付

用户扫码确认支付，整个流程为微信客户端与微信支付中心的交互。

#### 4 支付结果通知

用户支付成功之后，微信支付中心会根据==notify_url==异步通知商家支付结果。

通知频率为`15s/15s/30s/3m/10m/20m/30m/30m/30m/60m/3h/3h/3h/6h/6h`

> 返回结果

|             名称             |      参数       |    类型    |
| :--------------------------: | :-------------: | :--------: |
|        ==返回状态码==        | ==return_code== | ==String== |
|          公众账号ID          |      appid      |   String   |
|            商户号            |     mch_id      |   String   |
|          随机字符串          |    nonce_str    |   String   |
|             签名             |      sign       |   String   |
|           业务结果           |   result_code   |   String   |
|           用户标识           |     openid      |   String   |
|           交易类型           |   trade_type    |   String   |
|           付款银行           |    bank_type    |   String   |
|            总金额            |    total_fee    |    int     |
|         现金支付金额         |    cash_fee     |    int     |
|        微信支付订单号        | transaction_id  |   String   |
|          商户订单号          |  out_trade_no   |   String   |
|         支付完成时间         |    time_end     |   String   |
|           返回信息           |   return_msg    |   String   |
|            设备号            |   device_info   |   String   |
|           错误代码           |    err_code     |   String   |
|         错误代码描述         |  err_code_des   |   String   |
|       是否关注公众账号       |  is_subscribe   |   String   |
|           货币种类           |    fee_type     |   String   |
|       现金支付货币类型       |  cash_fee_type  |   String   |
|     代金券或立减优惠金额     |   coupon_fee    |   String   |
|   代金券或立减优惠使用数量   |  coupon_count   |   String   |
|      代金券或立减优惠ID      |  coupon_id_$n   |   String   |
| 单个代金券或立减优惠支付金额 |  coupon_fee_$n  |   String   |
|          商家数据包          |     attach      |   String   |

> 时序图

<img src="https://i.loli.net/2021/01/03/fQ4wLqOP8yS5lev.png" style="zoom: 50%;" />

### 支付宝支付

> 时序图

<img src="https://i.loli.net/2021/01/03/WropT9Z7hvGLnE3.png" style="zoom: 80%;" />

### 对比

支付宝支付会比微信支付多了一次==同步returnUrl==，但此returnUrl不能作为支付成功的凭证。无论微信支付还是支付宝支付都是以==异步通知为准==。

<img src="https://i.loli.net/2021/01/09/JYQNyz5tf7CIPZs.png" alt="image-20210109121058999" style="zoom:50%;" />

## 9. 个人中心

### 9. 1 用户信息修改

借助validation做用户信息校验。

### 9.2 头像上传

> 静态资源访问

1. `dev` : 本地静态资源映射。

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")  // 映射swagger2
                .addResourceLocations("file:C:\\@D\\-Development\\Study\\Codes\\java-idea\\Learning\\Project\\foodie-dev-git\\");     // 映射本地静态资源
    }
}
```

2. `prod` : 借助oss来存储静态资源。

> 注意事项

1. 后缀名校验（前后端均要），防止服务器被攻击。
2. 上传文件大小限制。（捕获异常，并返回错误提示）

```java
@RestControllerAdvice
public class CustomExceptionHandler {
    
    /**
     * 上传文件超过500k, 捕获此异常
     * @param e
     * @return
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public IMOOCJSONResult handlerMaxUploadFile(MaxUploadSizeExceededException e) {
        return IMOOCJSONResult.errorMsg("文件上传大小不能超过500k, 请压缩图片或者降低图片质量后再上传!");
    }
}
```

### 9.3 订单管理

> 列表展示

==BUG: 注意使用PageHelper进行分页查询导致订单管理分页不正确问题。==

由于订单管理中的订单查询为嵌套分页查询，所以前端展示时分页的数目按商品条数分页，不是按订单条数进行分页，所以导致商品展示的总条数不一致。(订单与商品条数是一对多的关系)

[PageHelper官方文档指明不支持嵌套查询的结果映射](https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/Important.md)。

<img src="https://i.loli.net/2021/01/16/32lXMko6dwEFxbI.png" alt="image-20210116143126237" style="zoom:50%;" />

<img src="https://i.loli.net/2021/01/16/9zbnFxHAhyaTPeY.png" alt="image-20210116143213280" style="zoom:50%;" />

`解决方法`

1. 前端进行订单的分页查询，拿到订单id后，再进行商品条目的查询。
2. 借助MyBatis的嵌套查询解决。

```java
@Data
public class MyOrdersVO {

    private String orderId;
    private Date createdTime;
    private Integer payMethod;
    private Integer realPayAmount;
    private Integer postAmount;
    private Integer isComment;
    private Integer orderStatus;

    private List<MySubOrderItemVO> subOrderItemList;
}
```

```java
@Data
public class MySubOrderItemVO {

    private String itemId;
    private String itemImg;
    private String itemName;
    private String itemSpecName;
    private Integer buyCounts;
    private Integer price;

}
```

```xml
<resultMap id="myOrdersVO" type="com.imooc.pojo.vo.MyOrdersVO">
  <id column="orderId" property="orderId"/>
  <result column="createdTime" property="createdTime"/>
  <result column="payMethod" property="payMethod"/>
  <result column="realPayAmount" property="realPayAmount"/>
  <result column="postAmount" property="postAmount"/>
  <result column="orderStatus" property="orderStatus"/>

  <collection property="subOrderItemList" select="getSubItems" column="orderId" ofType="com.imooc.pojo.vo.MySubOrderItemVO">
    <result column="itemId" property="itemId"/>
    <result column="itemName" property="itemName"/>
    <result column="itemImg" property="itemImg"/>
    <result column="itemSpecName" property="itemSpecName"/>
    <result column="buyCounts" property="buyCounts"/>
    <result column="price" property="price"/>
  </collection>
</resultMap>

<select id="queryMyOrders"  parameterType="Map" resultMap="myOrdersVO">
  SELECT
  od.id as orderId,
  od.created_time as createdTime,
  od.pay_method as payMethod,
  od.real_pay_amount as realPayAmount,
  od.post_amount as postAmount,
  os.order_status as orderStatus
  FROM
  orders od
  LEFT JOIN order_status os ON od.id = os.order_id
  WHERE
  user_id = #{paramsMap.userId}
  AND od.is_delete = 0
  <if test="paramsMap.orderStatus != null">
    AND order_status = #{paramsMap.orderStatus}
  </if>
  ORDER BY
  od.updated_time ASC
</select>

<select id="getSubItems" parameterType="String" resultType="com.imooc.pojo.vo.MySubOrderItemVO">
  SELECT
  oi.item_id as itemId,
  oi.item_name as itemName,
  oi.item_img as itemImg,
  oi.item_spec_name as itemSpecName,
  oi.buy_counts as buyCounts,
  oi.price as price
  FROM
  order_items as oi
  WHERE
  oi.order_id = #{orderId}
</select>

<!-- 嵌套查询, 分页结果不正确 -->
<select id="queryMyOrdersDoNotUse"  parameterType="Map" resultMap="myOrdersVO">
  SELECT
  od.id as orderId,
  od.created_time as createdTime,
  od.pay_method as payMethod,
  od.real_pay_amount as realPayAmount,
  od.post_amount as postAmount,
  os.order_status as orderStatus,
  oi.item_id as itemId,
  oi.item_name as itemName,
  oi.item_img as itemImg,
  oi.item_spec_name as itemSpecName,
  oi.buy_counts as buyCounts,
  oi.price as price
  FROM
  orders od
  LEFT JOIN order_status os ON od.id = os.order_id
  LEFT JOIN order_items oi ON od.id = oi.order_id
  WHERE
  user_id = #{paramsMap.userId}
  AND od.is_delete = 0
  <if test="paramsMap.orderStatus != null">
    AND order_status = #{paramsMap.orderStatus}
  </if>
  ORDER BY
  od.updated_time ASC
</select>
```

> 订单状态修改

需验证用户是否与该订单关联。

> 商品评论