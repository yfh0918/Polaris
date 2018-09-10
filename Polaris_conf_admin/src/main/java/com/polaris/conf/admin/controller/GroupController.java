package com.polaris.conf.admin.controller;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.conf.admin.core.model.ConfGroup;
import com.polaris.conf.admin.core.model.ConfNode;
import com.polaris.conf.admin.core.util.ReturnT;
import com.polaris.conf.admin.dao.ConfGroupDao;
import com.polaris.conf.admin.dao.ConfNodeDao;

/**
 * conf group controller
 */
@Controller
@RequestMapping("/group")
public class GroupController {
	
	@Autowired
	private ConfGroupDao confGroupDao;
	@Autowired
	private ConfNodeDao confNodeDao;

	@RequestMapping
	public String index(Model model) {

		List<ConfGroup> list = confGroupDao.findAll();

		model.addAttribute("list", list);
		return "group/group.index";
	}

	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(ConfGroup confGroup){

		// valid
		if (confGroup.getGroupName()==null || StringUtils.isBlank(confGroup.getGroupName())) {
			return new ReturnT<String>(500, "请输入GroupName");
		}
		if (confGroup.getGroupName().length()<4 || confGroup.getGroupName().length()>100) {
			return new ReturnT<String>(500, "GroupName长度限制为4~100");
		}
		if (confGroup.getGroupTitle()==null || StringUtils.isBlank(confGroup.getGroupTitle())) {
			return new ReturnT<String>(500, "请输入应用名称");
		}

		// valid repeat
		ConfGroup groupOld = confGroupDao.load(confGroup.getGroupName());
		if (groupOld!=null) {
			return new ReturnT<String>(500, "App Code对应应用以存在,请勿重复添加");
		}

		int ret = confGroupDao.save(confGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(ConfGroup confGroup){

		// valid
		if (confGroup.getGroupName()==null || StringUtils.isBlank(confGroup.getGroupName())) {
			return new ReturnT<String>(500, "请输入GroupName");
		}
		if (confGroup.getGroupName().length()<4 || confGroup.getGroupName().length()>100) {
			return new ReturnT<String>(500, "GroupName长度限制为4~100");
		}
		if (confGroup.getGroupTitle()==null || StringUtils.isBlank(confGroup.getGroupTitle())) {
			return new ReturnT<String>(500, "请输入应用名称");
		}

		int ret = confGroupDao.update(confGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(String groupName){

		// valid
		List<ConfNode> confNodeList = confNodeDao.findList(null, groupName, null);
		if (confNodeList.size() > 0) {
			return new ReturnT<String>(500, "该应用使用中, 不可删除");
		}

		int ret = confGroupDao.remove(groupName);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

}
