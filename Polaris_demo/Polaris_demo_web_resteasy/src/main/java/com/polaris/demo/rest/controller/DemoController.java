package com.polaris.demo.rest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.polaris.demo.service.ComboService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
//import io.swagger.models.Response;

/**
 * 认证模块
 *
 * @return
 */
@Component
@Api(value = "/rest/demo", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Path("/rest/demo")
public class DemoController {


	@Value("${test.password}")
	private String ssss;
	
	@Value("${bbbb.ccc}")
	private String dadfa;
	
	@Value("${test0.test2}")
	private String test02;
	
	@Value("${test0.test3}")
	private String test03;
	
	@Value("${test1.test2}")
	private String test12;
	
	@Value("${test1.test3}")
	private String test13;
	

	
//	@Autowired
//	private ComboService comboService;
    /**
     * 用户登入
     *
     * @return
     */
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @POST
    @Path("/test")
    @ApiOperation(value = "获取一个订单", notes = "返回一个订单", response = String.class)
    public String demotest(@Context HttpServletRequest request) {
    	System.out.println("ssss"+ssss);
        System.out.println("hello1234");

        System.out.println("hello1234"+test02 +test03+test12+test13);
        
        System.out.println("test"+dadfa);
//        comboService.findAll();
        return "hello1234";
    }
    
//    @GET
//    @Path("/{param}")
//    @ApiOperation(value = "Returns param", notes = "Returns param", response = DemoController.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successful retrieval of param value", response = DemoController.class) })
//    public Response printMessage(@PathParam("param") String msg) {
//    	System.out.println("ssss"+ssss);
//        String result = "Hello " + msg + "!";
//        return Response.status(200).entity(result).build();
//    }
    

}
