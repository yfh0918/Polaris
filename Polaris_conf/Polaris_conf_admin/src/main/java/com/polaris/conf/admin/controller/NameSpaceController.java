package com.polaris.conf.admin.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.comm.util.StringUtil;
import com.polaris.conf.admin.core.util.AdminSupport;
import com.polaris.conf.admin.core.util.ReturnT;

/**
 * conf Address controller
 */
@Controller
@RequestMapping("/namespace")
public class NameSpaceController {
	
	@RequestMapping
	public String index(Model model) {
		model.addAttribute("list", AdminSupport.getAllNameSpaces());
		return "namespace/namespace.index";
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(String namespace){
		synchronized (this){

			// valid
			if (StringUtil.isEmpty(namespace)) {
				return new ReturnT<String>(500, "请输入namespace名称");
			}
			if (namespace.length() < 1 || namespace.length() > 100) {
				return new ReturnT<String>(500, "namespace长度限制为1~100");
			}

			// valid repeat
			List<String> list = AdminSupport.getAllNameSpaces();
			if (list.contains(namespace)) {
				return new ReturnT<String>(500, "namespace已存在,请勿重复添加");
			}

			AdminSupport.addNameSpace(namespace);
			return ReturnT.SUCCESS;
		}
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(String namespace){

		// valid
		boolean result = AdminSupport.deleteNameSpace(namespace);
		if (!result) {
			return new ReturnT<String>(500, "该namespace节点使用中, 不可删除");
		}

		return ReturnT.SUCCESS;
	}
}
