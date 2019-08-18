package com.polaris.demo.rest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.core.util.LogUtil;
/**
 * 上传图片
 * @author liuweitao
 *
 */
@Controller
@RequestMapping("/upload")
public class UploadController {
	
	@RequestMapping(value = "/uploadBase64Img", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String uploadBase64Img(@Context HttpServletRequest request) {

       
		return "hello1234";
    }
	

	
}
