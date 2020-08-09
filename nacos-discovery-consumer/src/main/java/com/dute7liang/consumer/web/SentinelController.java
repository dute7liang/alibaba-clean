package com.dute7liang.consumer.web;

import com.dute7liang.consumer.api.ProviderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sentinel")
public class SentinelController {

    @Autowired
    private ProviderClient providerClient;

    @GetMapping("/test1")
    public String test1(){
        return providerClient.echo("sentinel test1 调用者!");
    }

}
