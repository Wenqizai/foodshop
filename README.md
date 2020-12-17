## 1. 登录与注册

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

```java
// Swagger官方路径
http://localhost:8088/swagger-ui.html
// 新路径
http://localhost:8088/doc.html
```

## 3. 日志管理

1. 整合slf4j+log4j，添加log4j.properties。
2. 借助Spring AOP用来记录每个service的执行时间。
   1. 执行时间>3s，输出日志级别为ERRO。
   2. 执行时间>2s，输出日志级别为WARN。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

3. 