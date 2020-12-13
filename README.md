## 1. 登录与注册

### 1. 1 常见的用户注册登录流程

![](C:\@D\-Development\Study\Codes\java-idea\Learning\Project\foodie-dev-git\图片存放\注册登录流程.png)

### 1.2 本项目采用方案

本次项目采用注册方案为两次校验密码的方式. 大致流程如下: 

```txt
注册 -> 提交注册信息 -> 后端校验信息 -> 设置默认信息并插库 -> 返回user对象, 用于页面显示
```

