package com.polaris.demo.rest.controller;

import com.polaris.extension.feign.FeignClient;

import feign.Request.Options;

public class FeignConsumer {
    
    public static void main(String[] args) throws Exception {
        
        //需要引入Polaris_extension_feign，也可以自定义解决方案，采用spi机制注入feign客户端
        //注解方式参考Polaris_demo_web_springmvc的DemoController 的remote方法，需要开启@EnableNamingRequest注解
        //一下是非注解方式
        User param = new User();
        param.setUsername("scott");
        FeignClient.Default(null, new Options(), null, null, null);//设置全局参数
        RemoteService service = FeignClient.target(RemoteService.class);
        for (int i = 1; i <= 10; i++) {
            User result = service.getOwner(param);
            System.out.println(result.getId() + "," + result.getUsername());
        }
    }

}
