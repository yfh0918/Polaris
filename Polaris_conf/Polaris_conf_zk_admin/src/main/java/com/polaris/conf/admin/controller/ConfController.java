package com.polaris.conf.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.core.util.StringUtil;
import com.polaris.conf.admin.controller.annotation.PermessionLimit;
import com.polaris.conf.admin.core.model.ConfNode;
import com.polaris.conf.admin.core.util.AdminSupport;
import com.polaris.conf.admin.core.util.ReturnT;

/**
 * 配置管理
 */
@Controller
@RequestMapping("/conf")
public class ConfController {
	
	@RequestMapping
	@PermessionLimit
	public String index(Model model) {
		model.addAttribute("namespaceList", AdminSupport.getAllNameSpaces());
		return "conf/conf.index";
	}

	@RequestMapping("/findGroup")
	@ResponseBody
	@PermessionLimit
	public List<String> findGroup(ConfNode confNode) {
		if (StringUtil.isEmpty(confNode.getNamespace())) {
			return new ArrayList<>();
		}
		return AdminSupport.getAllGroups(confNode.getNamespace());
	}
	
	@RequestMapping("/findList")
	@ResponseBody
	@PermessionLimit
	public Map<String, Object> findList(ConfNode confNode) {
		
		List<ConfNode> result = new ArrayList<>();
		if (StringUtil.isNotEmpty(confNode.getNamespace()) && StringUtil.isNotEmpty(confNode.getGroup())) {
			List<String> list = AdminSupport.getAllKeys(confNode.getNamespace(), confNode.getGroup());
			for (String key : list) {
				ConfNode confC = new ConfNode();
				confC.setGroup(confNode.getGroup());
				confC.setNamespace(confNode.getNamespace());
				confC.setKey(key);
				confC.setValue(AdminSupport.getKey(confNode.getNamespace(), confNode.getGroup(), key));
				result.add(confC);
			}
		}
		
		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("data", result);
		maps.put("recordsTotal", result.size());		// 鎬昏褰曟暟
		maps.put("recordsFiltered", result.size());	
				
		return maps;
	}
	
	/**
	 * get
	 * @return
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@PermessionLimit
	public ReturnT<String> delete(ConfNode confNode){
		if (StringUtil.isEmpty(confNode.getNamespace()) || 
				StringUtil.isEmpty(confNode.getGroup()) || 
				StringUtil.isEmpty(confNode.getKey())) {
			return new ReturnT<String>(500, "参数错误");
		}
		// valid
		AdminSupport.deleteKey(confNode.getNamespace(), confNode.getGroup(), confNode.getKey());
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
		if (StringUtil.isEmpty(confNode.getNamespace()) || 
				StringUtil.isEmpty(confNode.getGroup()) ) {
			return new ReturnT<String>(500, "参数错误");
		}

		// valid
		if (StringUtils.isBlank(confNode.getKey())) {
			return new ReturnT<String>(500, "请输入key");
		}
		if (confNode.getKey().length()<1 || confNode.getKey().length()>100) {
			return new ReturnT<String>(500, "key长度限制为1~100");
		}
		if (confNode.getValue() != null && confNode.getValue().length()>1000) {
			return new ReturnT<String>(500, "value长度限制为1~1000");
		}
		List<String> list = AdminSupport.getAllKeys(confNode.getNamespace(), confNode.getGroup());
		if (list.contains(confNode.getKey())) {
			return new ReturnT<String>(500, "key已存在,请勿重复添加");
		}
		AdminSupport.addKey(confNode.getNamespace(), confNode.getGroup(), confNode.getKey(), confNode.getValue());
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
		if (StringUtil.isEmpty(confNode.getNamespace()) || 
				StringUtil.isEmpty(confNode.getGroup()) || 
				StringUtil.isEmpty(confNode.getKey())) {
			return new ReturnT<String>(500, "参数错误");
		}
		
		if (confNode.getValue() != null && confNode.getValue().length()>1000) {
			return new ReturnT<String>(500, "value长度限制为1~1000");
		}
		AdminSupport.addKey(confNode.getNamespace(), confNode.getGroup(), confNode.getKey(), confNode.getValue());


		return ReturnT.SUCCESS;
	}



}
