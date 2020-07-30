package com.dute7liang.provider.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>created on 2020-7-30</p>
 *
 * @author dute7liang
 */
@RestController
public class DemoController {

    @GetMapping(value = "/echo/{string}")
    public String echo(@PathVariable String string) {
        return "Hello Nacos Discovery " + string;
    }
}
