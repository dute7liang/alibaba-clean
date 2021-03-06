package com.dute7liang.provider.web;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>created on 2020-7-30</p>
 *
 * @author dute7liang
 */
@RestController
@Slf4j
@RequestMapping("provider")
public class ProviderController {

    @GetMapping(value = "/echo/{string}")
    public String echo(@PathVariable String string) {
        log.warn("我被调用了！");
        return "Hello Nacos Discovery " + string;
    }

    @GetMapping(value = "sentinel1")
    public String sentinel1(String name){
        log.warn("我被调用了");
        return name;
    }
}
