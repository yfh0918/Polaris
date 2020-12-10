package com.polaris.demo.rest.controller;

import com.polaris.extension.feign.FeignClient;

import feign.Request.Options;

public class FeignConsumer {
    
    public static void main(String[] args) throws Exception {
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
