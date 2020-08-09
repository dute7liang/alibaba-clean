# Spring Cloud Alibaba Clean

## Nacos

元数据：服务级别、集群级别、实例级别

## Ribbon

主要在官方文档中截取：[SpringCloud Netflix Ribbon](https://cloud.spring.io/spring-cloud-static/spring-cloud-netflix/2.1.5.RELEASE/single/spring-cloud-netflix.html#spring-cloud-ribbon)

### Ribbon的组成

|接口|作用|默认值|
|---|---|---|
|IClientConfig|读取配置|DefaultClientConfigImpl|
|IRule|负载均衡规则|ZoneAvoidanceRule|
|IPing|筛选掉ping不通的实例|DummyPing|
|ServerList<Server>|Ribbon获取的实例列表|Ribbon默认实现 ConfigurationBasedServerList<br>Alibaba默认实现 NacosServerList|
|ServerListFilter<Server>|过滤器，可以过滤掉不符合规则的实例|dasda|
|ILoadBalancer|Ribbon入口|ZonePreferenceServerListFilter|
|ServerListUpdater|更新Ribbon中list的接口|ZoneAwareLoadBalancer|

### Ribbon默认的负载均衡

- AvailabilityFilteringRule 过滤标记失败的实例，可以理解为：对每个server运行状态会做标记。从而过滤掉不健康的实例
- BestAvailableRule 选择一个最小并发的实例
- RandomRule 随机获取一个
- WeightedResponseTimeRule 根据接口的响应时间为权重过滤，响应时间越长，权重越少，选中概率越低
- RetryRule 由于IRule可以级联配置，可以对现有的策略加添加一个重制机制
- RoundRobinRule 轮训
- ZoneAvoidanceRule 根据区域来选择服务。在没有区域概念的情况下，实际就是和`RoundRobinRule`类似

### Ribbon在Spring Cloud中的配置

在测试中发现代码配置的优先级比配置文件配置的优先级高

#### Java 代码配置

```java
/**
 * 默认的负载均衡
 */
//@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
/**
 * 指定service 为某个负载均衡
 */
@RibbonClient(value = "nacos-provider",configuration = RibbonConfiguration.class)
public class DefaultRibbonRuleConfig {

}

/** 必须注意改类不能被Spring给扫描到，否则会发现这里的配置会默认使用的全局配置
这里有Spring父子上下文的概念，类似于Spring SpringMvc的父子上下文的理解*/
@Configuration
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        return new NacosRule();
    }
}
```

#### 配置文件配置

配置文件不支持配置全局配置

格式如下

`<clientName>.ribbon.` 开头

- NFLoadBalancerClassName: ILoadBalancer的实现类
- NFLoadBalancerRuleClassName: IRule的实现类
- NFLoadBalancerPingClassName: IPing的实现类
- NIWSServerListClassName: ServerList的实现类
- NIWSServerListFilterClassName: ServerListFilter的实现类

```yaml
nacos-provider:
  ribbon:
    NIWSServerListClassName: com.netflix.loadbalancer.ConfigurationBasedServerList
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.WeightedResponseTimeRule
```

### Ribbon的饥饿加载的问题

```yaml
# 表示ribbon是否启用饥饿加载。 默认不开启，则第一次访问会相对比较慢
ribbon:
  eager-load:
    enabled: true
    clients: ssss,ssss
```

### Ribbon和Nacos的结合使用

在Nacos有很多参数，比如权重、区域等配置。在和Ribbon使用的可以通过其参数来更加灵活的获取想要的服务

比如SpringCloud Ribbon不支持权重，在Nacos中有支持对IRule的实现扩展。

- NacosRule 可以实现同一集群的优先调用。

可以自己去扩展IRule实现类

Nacos的namespace可以实现不同的环境隔离调用，比如开发环境、测试环境等等

```yaml
spring:
  cloud:
    nacos:
      discovery:
       namespace: dev(这里实际是一个uuid类似的。要去控制生成)
```

## Feign

### Feign的细粒度配置

Feign支持Java代码配置和配置文件配置。这里只列出配置文件配置

```yaml
feign:
  client:
    config:
      default: # 默认配置
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: basic
      nacos-provider: # 单独的配置
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: full # 日志级别
        errorDecoder: com.example.SimpleErrorDecoder # 指定错误解码器
        retryer: com.example.SimpleRetryer # 指定重试策略
        requestInterceptors: # 拦截器
          - com.example.FooRequestInterceptor
          - com.example.BarRequestInterceptor
        decode404: false # 是否404错误解码
        encoder: com.example.SimpleEncoder # 编码器
        decoder: com.example.SimpleDecoder # 解码器
        contract: com.example.SimpleContract # 注解支持，默认SpringMVC
```

超时、限流、仓壁模式、断路器模式

## Sentinel

`/actuator/sentinel` actuator的暴露点

### Sentinel 控制台的使用

#### 限流

流控模式

- 直接 检测自己的APi
- 关联 如果关联的接口达到限流
- 链路 也就是针对API来源的限流

流控效果

- 快速失败  抛出异常
- WarmUp 指的是根据多少秒慢慢的达到峰值
- 排队等待 保证接口的QPS均匀的处理。如果超时时间超出则抛出异常

#### 降级

- RT  在平均响应时间（秒级统计）超出阈值 && 时间窗口内通过的请求 >= 5  -> 触发降级 -> 时间窗口结束 -> 关闭降级
- 异常比例 QPS >=5 && 异常比例（秒级统计）超出阈值 -> 触发降级 -> 时间窗口结束 -> 关闭降级
- 异常数 异常数（分钟统计）超出阈值 -> 触发降级 -> 时间窗口结束 -> 关闭降级

> RT默认最大的值为4900毫秒

#### 热点规则

热点规则可以对入参进行细粒度的限流，需要使用`@SentinelResource`标记

### Sentinel 代码的配置

### Sentinel 自定义限流代码

```java
Entry entry = null;
try{
    entry = SphU.entry("test1");
    // 自己的逻辑
    return "返回正确的数据";
}catch(BlackException e){
    log.warn("限流了!");
    return "返回限流后的数据";
}finally{
    if(entry != null){
        entry.exit();
    }
}
```

也可以直接使用`@SentinelResource`注解搞定

### Sentinel 的持久化配置

### Sentinel 整合 RestTemplate

`@SentinelRestTemplate`

### Sentinel 整合 Feign

`feign.sentinel.enabled=true`

## MQ

### Spring异步调用

`@Async`,`WebClient`

## Gataway

### 概念

- Route、路由
- Predicate 谓词 用于判断路由的正确性
- Filter 过滤器

### 整合Sentinel

### 监控

### 官网限流

内置，令牌桶-基于Redis
