package com.dute7liang.consumer.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>created on 2020-8-14</p>
 *
 * @author dute7liang
 */
@RestController
@RequestMapping("config")
@RefreshScope
public class ConfigTestController {

    @Value("${test.name:default}")
    private String name;

    @GetMapping("name")
    public String test(){
        return name;
    }

}
