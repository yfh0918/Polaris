package com.polaris.demo.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.polaris.container.servlet.util.RequestUtil;
import com.polaris.core.util.JacksonUtil;
import com.polaris.demo.DemoLifeCycle;
import com.polaris.demo.config.TestProperties;
import com.polaris.demo.config.TestProperties.InnerA;
/**
 * 
 * @author yufenghua
 *
 */
@Controller
@RequestMapping("/demo")
public class DemoController {
	private static final Logger logger = LoggerFactory.getLogger(DemoController.class);
	
	@Autowired
	private TestProperties testProperties;
	
	@Autowired
	private RemoteService remoteService;
	
	@Autowired
    private RemoteService2 remoteService2;
	
	@RequestMapping(value = "/test", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String uploadBase64Img(HttpServletRequest request)throws Exception  {
	    Map<String, Object> requestMap = RequestUtil.convertParameterToMap(request);
	    System.out.println(JacksonUtil.toJson(requestMap));
	    TestProperties xxxx = RequestUtil.convertParameterToObject(request,TestProperties.class);
        System.out.println(JacksonUtil.toJson(xxxx));
        
		logger.info(testProperties.getAddress1());
		logger.info(testProperties.getPassword());
		//logger.info(testProperties.getDigit());
		//logger.info(testProperties.isOk());
		for (InnerA linnera : testProperties.getList()) {
			logger.info(linnera.getAddress1());
		}
		logger.info(testProperties.getInnerA().getAddress1());
		//HttpClientUtil.get("http://localhost:9077/demowebnodubbo/rest/demo/test2");
		List<TestProperties> lista = new ArrayList<>();
		lista.add(testProperties);
		testProperties.setAddress1(null);
		String listss = JacksonUtil.toJson(lista);
		String ss = JacksonUtil.toJson(testProperties);
		System.out.println(ss);
		//TestProperties abc = JacksonUtil.toObj(ss, TestProperties.class);
		Map<String, Object> abcd = JacksonUtil.toObj(ss, new TypeReference<Map<String, Object>>(){});
		System.out.println(JacksonUtil.toJson(abcd));
        List<Map<String, Object>> abcde = JacksonUtil.toObj(listss, new TypeReference<List<Map<String, Object>>>(){});
        System.out.println(JacksonUtil.toJson(abcde));
		return "aa";
    }
	
	@RequestMapping(value = "/test/startLifeCycle", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String startLifeCycle(HttpServletRequest request)throws Exception  {
		DemoLifeCycle lifeCycle = new DemoLifeCycle();
		lifeCycle.start();
		return "aa";
    }
	
	
	@RequestMapping(value = "/remote", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String remote(HttpServletRequest request)throws Exception  {
	    User param = new User();
        param.setUsername("scott");
        for (int i = 1; i <= 10; i++) {
            User result = remoteService.getOwner(param);
            System.out.println(result.getId() + "1," + result.getUsername());
            result = remoteService2.getOwner(param);
            System.out.println(result.getId() + "2," + result.getUsername());
        }
        return "aa";
    }
	
}
