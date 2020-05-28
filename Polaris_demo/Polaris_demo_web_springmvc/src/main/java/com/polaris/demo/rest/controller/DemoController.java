package com.polaris.demo.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.core.util.HttpClientUtil;
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
	
	
	@RequestMapping(value = "/test", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String uploadBase64Img(HttpServletRequest request)throws Exception  {
		logger.info(testProperties.getAddress1());
		logger.info(testProperties.getPassword());
		//logger.info(testProperties.getDigit());
		//logger.info(testProperties.isOk());
		for (InnerA linnera : testProperties.getList()) {
			logger.info(linnera.getAddress1());
		}
		logger.info(testProperties.getInnerA().getAddress1());
		HttpClientUtil.get("http://localhost:9077/demowebnodubbo/rest/demo/test2");
		return "aa";
    }
	
	@RequestMapping(value = "/test/startLifeCycle", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String startLifeCycle(HttpServletRequest request)throws Exception  {
		DemoLifeCycle lifeCycle = new DemoLifeCycle();
		lifeCycle.start();
		return "aa";
    }
	

	
}
