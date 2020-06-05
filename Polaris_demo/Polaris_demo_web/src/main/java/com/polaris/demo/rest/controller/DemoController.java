package com.polaris.demo.rest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

	@Autowired
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
    public String demotest(@Context HttpServletRequest request) {

        //定义返回对象
        demoEntryIF.test2(new DemoDto());
        demoEntryIF.test(new DemoDto());
        System.out.println("hello1234");

        //返回结果
        return null;
    }
    
}
