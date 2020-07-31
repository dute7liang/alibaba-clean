package com.dute7liang.consumer.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>created on 2020-7-31</p>
 *
 * @author dute7liang
 */
@FeignClient(name = "nacos-provider")
public interface ProviderDemoClient {

    @GetMapping(value = "/echo/{string}")
    String echo(@PathVariable(name = "string") String string);

}
