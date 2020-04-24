package com.polaris.demo.rest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.polaris.core.util.HttpClientUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 认证模块
 *
 * @return
 */
@Component
@Api(value = "/rest/demo", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
@Path("/rest/demo")
public class DemoController {
	private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

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
    public String demotest(@Context HttpServletRequest request) throws Exception {
    	logger.info("ssss"+ssss);
    	logger.info("hello1234");

    	logger.info("hello1234"+test02 +test03+test12+test13);
        
    	logger.info("test"+dadfa);
        HttpClientUtil.get("http://localhost:9045/demospringmvc/demo/test");
//        comboService.findAll();
        return "hello1234";
    }
    

    
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @POST
    @Path("/test2")
    @ApiOperation(value = "获取一个订单", notes = "返回一个订单", response = String.class)
    public String test2(@Context HttpServletRequest request) {
    	logger.info("test2"+ssss);
    	logger.info("test2");
    	logger.info("test2"+test02 +test03+test12+test13);
    	logger.info("test2"+dadfa);
        return "aaaa";
    }
    

}
