# 1. 分布式会话

1. 登录和注册：设置token，存入redis。
2. 退出登录，清除redis中的token。

> Spring Session + Redis

引入依赖:

```xml
<!-- 引入Spring-session依赖 -->
<dependency>
  <groupId>org.springframework.session</groupId>
  <artifactId>spring-session-data-redis</artifactId>
</dependency>

<!-- 引入Spring安全框架 -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

添加配置:

```yml
Spring: 
  session:
      store-type: redis
```

Spring Session + Redis相对于传统的，完全Spring管理的session（`request.getSession()`）。好处是更加灵活地应用到分布式会话，解耦微服务。比如，使用Spring Session + Redis可以应用到不同语言开发的微服务中，此时的session不依赖Spring框架而存在，应用更加灵活和广发。

> 拦截器完成权限认证

前端以header形式传送userId + token，经拦截器拦截，从Redis中取出token进行校验， 一致则放行，不一致则返回错误信息给前端。