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

## CAS系统实现单点登录

> 首次登录

![image-20210304211425445](https://i.loli.net/2021/03/04/DzT4m25QLM1nPsw.png)

1. 初次访问
2. 验证是否登录：前端检查是否存在user的cookie
3. 携带returnUrl跳转至CAS：returnUrl为登录后回跳页面，即前端的首页
4. 验证未登录：CAS返回登录页面login
5. 用户名和密码登录：用户输入相关信息
6. 登录成功：验证用户名和密码成功
7. 创建用户会话：生成唯一token，将token信息设置到用户信息里面，并保存再Redis中作为用户的会话

```java
String uniqueToken = UUID.randomUUID().toString().trim();
UserVO userVO = new UserVO();
BeanUtils.copyProperties(userResult, userVO);
userVO.setUserUniqueToken(uniqueToken);
String userId = userResult.getId();
redisOperator.set(REDIS_USER_TOKEN + ":" + userId, JsonUtils.objectToJson(userVO));
```

8. 创建用户全局门票：创建唯一ticket，设置到Redis和cookie中，用于验证后获取用户的信息

```java
setCookie(COOKIE_USER_TICKET, userTicket, response);
redisOperator.set(REDIS_USER_TICKET + ":" + userTicket, userId);
```

9. 创建用户临时门票：创建唯一tmpticket，设置过期时间，返回前端。用于跨站登录的唯一凭证。
10. 回跳并返回临时票据：CAS根据前端发送的returnUrl，将临时票据返回给前端保存。
11. 检查临时票据：前端检查有临时票据，发起检查临时票据请求。
12. 校验成功：CAS检查临时票据，若未失效则删除此票据，获取cookie中的userId，并从Redis中获取用户的会话信息，返回给前端。

==注意：后端先检查临时票据有效，再检查全局门票有效，检查完临时票据需将它删除，下次登录时重新生成。==

> 再次登录

![image-20210304212106800](https://i.loli.net/2021/03/04/yFSZQKHr3vOjPNL.png)

==注意：再次登录时，需要先查看是否存在全局门票，如果存在则表明已经登录过，直接创建临时票据返回即可。==

> 退出登录

cookie中获取全局门票 -> 删除cookie -> Redis中删除全局门票 -> Redis中删除全局会话