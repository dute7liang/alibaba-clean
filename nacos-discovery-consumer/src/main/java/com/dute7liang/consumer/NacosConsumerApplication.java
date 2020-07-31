package com.dute7liang.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * <p>created on 2020-7-30</p>
 *
 * @author dute7liang
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class NacosConsumerApplication {

    public static void main(String[] args) {

        SpringApplication.run(NacosConsumerApplication.class,args);

    }

    //实例化 RestTemplate 实例
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
