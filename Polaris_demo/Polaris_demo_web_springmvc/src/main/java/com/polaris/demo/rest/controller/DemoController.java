package com.polaris.demo.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 
 * @author yufenghua
 *
 */
@Controller
@RequestMapping("/demo")
public class DemoController {
	
	@RequestMapping(value = "/test", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String uploadBase64Img(HttpServletRequest request) {
		return "hello1234";
    }
	

	
}
