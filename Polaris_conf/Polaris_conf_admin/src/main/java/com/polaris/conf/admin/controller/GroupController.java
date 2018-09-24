package com.polaris.conf.admin.controller;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.comm.util.StringUtil;
import com.polaris.conf.admin.core.model.ConfGroup;
import com.polaris.conf.admin.core.util.AdminSupport;
import com.polaris.conf.admin.core.util.ReturnT;

/**
 * conf group controller
 */
@Controller
@RequestMapping("/group")
public class GroupController {
	
	@RequestMapping
	public String index(Model model) {
		model.addAttribute("namespaceList", AdminSupport.getAllNameSpaces());
		return "group/group.index";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/findList")
	@ResponseBody
	public ReturnT<String> findList(String namespace){

		if (StringUtil.isNotEmpty(namespace)) {
			List<String> list = AdminSupport.getAllGroups(namespace);
			return new ReturnT(list);
		}
		return ReturnT.SUCCESS;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(ConfGroup group){

		// valid
		if (group.getGroup()==null || StringUtils.isBlank(group.getGroup())) {
			return new ReturnT<String>(500, "请输入group");
		}
		if (group.getGroup().length()<1 || group.getGroup().length()>100) {
			return new ReturnT<String>(500, "group长度限制为1~100");
		}
		List<String> list = AdminSupport.getAllGroups(group.getNamespace());
		if (list.contains(group.getGroup())) {
			return new ReturnT<String>(500, "group已存在,请勿重复添加");
		}
		AdminSupport.addGroup(group.getNamespace(), group.getGroup());
		list = AdminSupport.getAllGroups(group.getNamespace());
		return new ReturnT(list);
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(ConfGroup group){

		// valid
		boolean result = AdminSupport.deleteGroup(group.getNamespace(), group.getGroup());
		if (!result) {
			return new ReturnT<String>(500, "该应用使用中, 不可删除");
		}
		List<String> list = AdminSupport.getAllGroups(group.getNamespace());
		return new ReturnT(list);
	}

}
