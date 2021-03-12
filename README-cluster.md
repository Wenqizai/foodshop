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

# 2. 分布式文件存储

单体文件上传问题：

1. 单向存储
2. 不支持集群
3. 文件数据冗余
4. 可拓展性差

==单机文件上传时，会将文件保存再某一服务器。当我们读取这文件时，由于Nginx负载均衡机制，请求可能发送到其他服务器，导致读取不到文件。==

## FastDFS

FastDFS是一个开源的分布式文件系统，她对文件进行管理，功能包括：**文件存储、文件同步、文件访问（文件上传、文件下载）**等，解决了大**容量存储和负载均衡**的问题。特别适合以文件为载体的在线服务，如相册网站、视频网站等等。

FastDFS服务端有两个角色：**跟踪器（tracker）和存储节点（storage）**。跟踪器主要做调度工作，在访问上起负载均衡的作用。

为了支持大容量，存储节点（服务器）采用了分卷（或分组）的组织方式。**存储系统由一个或多个卷组成，卷与卷之间的文件是相互独立的**，**所有卷 的文件容量累加就是整个存储系统中的文件容量**。一个卷可以由一台或多台存储服务器组成，一个卷下的存储服务器中的文件都是相同的，**卷中的多台存储服务器起到了冗余备份和负载均衡的作用**。

在卷中增加服务器时，同步已有的文件由系统自动完成，**同步完成后，系统自动将新增服务器切换到线上提供服务**。

当存储空间不足或即将耗尽时，可以动态添加卷。只需要增加一台或多台服务器，并将它们配置为一个新的卷，这样就扩大了存储系统的容量。

> 架构图

卷Group提供容量存储，Group里面包含多个存储节点Node，其起到冗余备份和负载均衡的作用。

![image-20210307164915149](https://i.loli.net/2021/03/07/ixX2DJGEpMklb79.png)

> 上传与下载

`上传`

![image-20210307165934004](https://i.loli.net/2021/03/07/YLu8GixM2EXm1bK.png)`下载`

![image-20210307170513518](https://i.loli.net/2021/03/07/Xfj8PJMpYk7i9Q5.png)

> 总结

1. 水平扩容容易
2. 运维复杂
3. 开发复杂

# 云存储阿里OSS

> 优点

1. SDK使用简单
2. 提供强大的文件处理功能(文件处理, 权限)
3. 零运维成本
4. 图形化管理控制台
5. CDN加速