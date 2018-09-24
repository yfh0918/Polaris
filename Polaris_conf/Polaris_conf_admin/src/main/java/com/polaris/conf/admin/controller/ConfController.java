package com.polaris.conf.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.conf.admin.controller.annotation.PermessionLimit;
import com.polaris.conf.admin.core.model.ConfGroup;
import com.polaris.conf.admin.core.model.ConfNode;
import com.polaris.conf.admin.core.util.AdminSupport;
import com.polaris.conf.admin.core.util.ReturnT;

/**
 * 配置管理
 */
@Controller
@RequestMapping("/conf")
public class ConfController {
	
	@RequestMapping("")
	@PermessionLimit
	public String index(Model model, String znodeKey){

		model.addAttribute("namespaceList", AdminSupport.getAllNameSpaces());
		return "conf/conf.index";
	}
	
	@RequestMapping("/findList")
	@ResponseBody
	@PermessionLimit
	public Map<String, String> findList(ConfNode confNode) {
		Map<String, String> result = new HashMap<>();
		
		return result;
	}
	
	/**
	 * get
	 * @return
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> delete(ConfNode confNode){
		return ReturnT.SUCCESS;
	}
	

	
	/**
	 * create/update
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> add(ConfNode confNode){
		return ReturnT.SUCCESS;
	}
	
	/**
	 * create/update
	 * @return
	 */
	@RequestMapping("/update")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> update(ConfNode confNode){
		return ReturnT.SUCCESS;
	}



}
