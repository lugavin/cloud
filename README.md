# Cloud

Spring Cloud APP

| Spring Boot | Spring Cloud |
| ----------- | ------------ |
| 1.5.x       | Dalston      |
| 1.5.x       | Edgware      |
| 2.0.x       | Finchley     |
| 2.1.x       | Greenwich    |
| 2.2.x       | Hoxton       |
| 2.3.x       | Hoxton       |

## Auth

| 资源编码    | 资源URI        | 资源请求方式 |
| ----------- | -------------- | ------------ |
| user:create | /api/users     | POST         |
| user:delete | /api/user/{id} | DELETE       |
| user:update | /api/user/{id} | PUT          |
| user:query  | /api/user/{id} | GET          |

在抽象成上述的映射关系后，我们的前后端的资源有了参照，我们对于用户组的权限授权就容易了。比如我授予一个用户增加、删除权限，**在前端我们只需要检验该资源编码的有无就可以控制按钮的显示和隐藏，而在后端我们只需要统一拦截判断该用户是否具有URI和对应请求方式即可。**至于权限的统一拦截是放置在Zuul这个网关上，还是落在具体的后端服务的拦截器上，都可以轻而易举地实现。要是权限的统一拦截放置在Zuul上会有一个问题，那就是后端服务安不安全，因为服务间调用只需要通过注册中心即可不会经过Zuul网关，这里就涉及到服务之间的鉴权。

服务之间的鉴权的一种实现方式是基于 Spring Cloud 的 FeignClient Inteceprot（自动申请服务token、传递当前上下文）和 MVC Inteceptor（服务token校验、更新当前上下文）来实现，从而对服务的安全性做进一步保护。

虽然通过上述的用户合法性检验、用户权限拦截以及服务之间的鉴权，保证了API接口的安全性，但是其间的Http访问频率是比较高的，请求数量上来的时候，慢的问题就会特别明显，可以考虑一定的优化策略，比如用户权限缓存、服务授权信息的派发与混存、定时刷新服务鉴权Token等。

<!--
## 源码分析

### Spring
### MyBatis
### Tomcat
### Netty

## 分布式

### 分布式协调服务(Zookeeper)
### 高性能网络通信(Netty)
### NoSQL数据库(MongoDB)
### 分布式缓存(Redis)
### 分布式搜索引擎(Elasticsearch)
### 分布式日志分析(ELK)
### 分布式消息通信(Kafka)
### 分布式任务调度平台(XXL-JOB)
### 分布式事务(RocketMQ|Seata)
### 分库分表(ShardingSphere|Mycat)

## 性能优化

### MySQL
### JVM
### Tomcat

## 网上商城
- 数据库表设计
- 全文检索(Elasticsearch)
- 消息中间件(Kafka)
- 分布式事务(RocketMQ)
- 授权中心(JWT&RSA)
- 购物车(Redis)
- 订单
- 库存
- 支付(AliPay&WxPay)
- 评论
- 短信
- 实时聊天(Netty&WebSocket)
-->