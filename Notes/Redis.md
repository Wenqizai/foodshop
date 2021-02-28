# 常用缓存方案

> Ehcache

优点：

1. 基于Java开发
2. 基于JVM缓存
3. 简单、轻巧、方便

缺点：

1. 不支持集群
2. 分布式不支持

适合：MyBatis、Hibernate

> Memcache

优点：

1. 简单的key-value存储（字符串）
2. 内存使用率比较高
3. 多核处理、多线程

缺点：

1. 无法容灾
2. 无法持久化

> Redis

优点：

1. 丰富的数据结构
2. 持久化
3. 主从同步、故障转移
4. 内存数据库

缺点：

1. 单核、单线程

# Redis架构

> 线程模型

![image-20210221174049483](https://i.loli.net/2021/02/21/6aufRclWBdhXT7O.png)

# 主从

本项目采用一主二从架构。

```sql
# Master
192.168.8.116
# Slave
192.168.8.117
192.168.8.118
```

操作命令：

```bash
# 查看主从信息
INFO replication
```

> 无磁盘化同步

当Redis进行主从同步时，我们可以配置无磁盘化复制。主从之间传送RDB文件均在内存之间进行，没有写入到磁盘中，可以提高传送的速度。

```txt
repl-diskless-sync yes
```

# 缓存不足

### 缓存过期机制

1. 定期删除（主动）
2. 惰性删除（被动）

### 内存淘汰管理机制

- MEMORY MANAGEMENT
- maxmemory