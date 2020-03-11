package com.polaris.demo.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.demo.config.TestProperties;
/**
 * 
 * @author yufenghua
 *
 */
@Controller
@RequestMapping("/demo")
public class DemoController {
	
	@Autowired
	private TestProperties testProperties;
	
	
	@RequestMapping(value = "/test", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String uploadBase64Img(HttpServletRequest request) {
		System.out.println(testProperties.getAddress1());
		System.out.println(testProperties.getPassword());
		System.out.println(testProperties.getDigit());
		System.out.println(testProperties.isOk());
		return "aa";
    }
	

	
}
