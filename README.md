## 1. 登录与注册

### 1. 1 常见的用户注册登录流程

![](C:\@D\-Development\Study\Codes\java-idea\Learning\Project\foodie-dev-git\图片存放\注册登录流程.png)

### 1.2 本项目采用方案

本次项目采用注册方案为两次校验密码的方式. 大致流程如下: 

```txt
注册 -> 提交注册信息 -> 后端校验信息 -> 设置默认信息并插库 -> 返回user对象, 用于页面显示
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