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
# 配置单机主从
replicaof <masterip> <masterport>
masterauth <master-password>
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

# 集群

本项目采用三主三从架构。

整备6台服务器，开启redis，并配置集群功能。

集群数据分布在槽slot中，共16384，分配到所有的master中。

`搭建集群:`

```txt
# redis.conf
cluster-enabled yes
cluster-config-file nodes-6379.conf
cluster-node-timeout 5000
# 开启集群
redis-cli -a redis --cluster create 192.168.8.130:6379 192.168.8.131:6379 
192.168.8.132:6379 192.168.8.133:6379 192.168.8.134:6379 192.168.8.135:6379 
--cluster-replicas 1
# 检查集群节点
redis-cli -a redis --cluster check 192.168.8.130:6379
```

`重新搭建集群：`

```
# 1. 删除文件
appendonly.aof dump.rdb nodes-6379.conf
# 2. 登录每个集群执行命令
flushdb
cluster reset
# 3. 重新启动集群
redis-cli -a redis --cluster create 192.168.8.130:6379 192.168.8.131:6379 
192.168.8.132:6379 192.168.8.133:6379 192.168.8.134:6379 192.168.8.135:6379 
--cluster-replicas 1
# 4. yes
```

## 缓存穿透，击穿和雪崩解决方案

`缓存穿透`

1. mysql查出来为null，并设置到redis中。
2. 布隆过滤器。（有误差）

`缓存雪崩`

1. 热点key，永不过期
2. 过期时间错开（过期时间加上随机值）
3. 多种缓存结合（请求 -> Redis -> Memcache -> MySQL）
4. 采购第三方Redis

## 命令

> 批量查询(提高系统吞吐量)

1. mget
2. pipeline（管道传输）

```java
redisTemplate.executePipelined(new RedisCallback<String>() {
  @Override
  public String doInRedis(RedisConnection connection) throws DataAccessException {
    // Redis的相关操作
    return null;
  }
});
```



