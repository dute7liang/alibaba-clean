package com.dute7liang.consumer.web;

import com.dute7liang.consumer.api.ProviderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * <p>created on 2020-7-30</p>
 *
 * @author dute7liang
 */
@RestController
@RequestMapping("consumer")
public class ConsumerController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProviderClient providerClient;

    @Value("${spring.application.name}")
    private String appName;

    /**
     * 返回所有服务的实例信息
     * @return
     */
    @GetMapping("serviceInstance")
    public List<ServiceInstance> serviceInstance(){
        // 查询服务的所有实例信息
        List<ServiceInstance> instances = discoveryClient.getInstances("nacos-provider");
        return instances;
    }

    /**
     * 传统RestTemplate方式调用远程接口, 没有引入Ribbon
     * @return
     */
    @RequestMapping("/message")
    public String getMessage(){
        List<ServiceInstance> instances = discoveryClient.getInstances("nacos-provider");
        // 没有负载均衡 直接获取第一个实例
        String url = instances.stream().map(i -> i.getUri().toString() + "/provider/echo/{string}")
                .findFirst().orElseThrow(() -> new IllegalArgumentException("实例不存在!"));
        // 随机获取一个实例
        List<String> collect = instances.stream().map(i -> i.getUri().toString() + "/provider/echo/{string}").collect(Collectors.toList());
        int i = ThreadLocalRandom.current().nextInt(collect.size());
        url = collect.get(i);
        // 注意这种写法RestTemplate不能加RestTemplate。
        // 因为加了RestTemplate有了ribbon需要传入服务名，它会自动通过负载均衡寻找服务
        String echo = restTemplate.getForObject(url, String.class, "使用RestTemplate不使用Ribbon");
        return echo;
    }

    /**
     * 使用RestTemplate来使用。使用Ribbon
     * @return
     */
    @RequestMapping("/message1")
    public String getMessage1(){
        // nacos-provider 就是服务名，Ribbon会自动找到实际的地址
        String echo = restTemplate.getForObject("http://nacos-provider/provider/echo/{string}", String.class, "使用RestTemplate+Ribbon");
        return echo;
    }

    /**
     * 使用feign方式，调用接口
     * @return
     */
    @RequestMapping("/message2")
    public String getMessage2(){
        return providerClient.echo("feign+ribbon 调用数据");
    }


}
