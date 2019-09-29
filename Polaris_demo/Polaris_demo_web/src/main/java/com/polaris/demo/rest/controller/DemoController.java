package com.polaris.demo.rest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.polaris.demo.api.dto.DemoDto;
import com.polaris.demo.api.service.DemoEntryIF;

/**
 * 认证模块
 *
 * @return
 */
@Component
@Path("/rest/demo")
public class DemoController {

	@Reference(url="dubbo://192.168.1.13:20880")
    private DemoEntryIF demoEntryIF;

    /**
     * 用户登入
     *
     * @return
     */
    @GET
    @POST
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject demotest(@Context HttpServletRequest request) {

        //定义返回对象
        demoEntryIF.test2(new DemoDto());
        demoEntryIF.test(new DemoDto());
        System.out.println("hello1234");

        //返回结果
        return null;
    }
    
}
