1. 登录与注册

### 1. 1 常见的用户注册登录流程

![](C:\@D\-Development\Study\Codes\java-idea\Learning\Project\foodie-dev-git\图片存放\注册登录流程.png)

### 1.2 本项目采用方案

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