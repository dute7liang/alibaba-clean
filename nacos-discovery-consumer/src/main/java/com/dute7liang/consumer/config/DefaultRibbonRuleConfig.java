package com.dute7liang.consumer.config;

import com.dute7liang.ribbon.RibbonConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;

/**
 * <p>created on 2020-7-31</p>
 *
 * @author dute7liang
 */
/**
 * 默认的负载均衡
 */
//@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
/**
 * 指定service 为某个负载均衡
 */
//@RibbonClient(value = "nacos-provider",configuration = RibbonConfiguration.class)
public class DefaultRibbonRuleConfig {

}
