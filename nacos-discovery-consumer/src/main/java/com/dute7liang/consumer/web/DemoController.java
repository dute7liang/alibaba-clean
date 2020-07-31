package com.dute7liang.consumer.web;

import com.dute7liang.consumer.api.ProviderDemoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * <p>created on 2020-7-30</p>
 *
 * @author dute7liang
 */
@RestController
public class DemoController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProviderDemoClient providerDemoClient;

    @Value("${spring.application.name}")
    private String appName;

    @RequestMapping("/message")
    public String getMessage(){
        // 传统 restTemplate 方式
//        ServiceInstance serviceInstance = loadBalancerClient.choose("nacos-provider");
//        String url = String.format("%S/echo/%s",serviceInstance.getUri().toString(),appName);
//        System.out.println("request url:"+url);
//        String echo = restTemplate.getForObject(url, String.class);

        // OpenFeign 方式
        String echo = providerDemoClient.echo(appName);
        return echo;
    }



}
