package com.polaris.conf.admin.controller;

import java.util.List;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.comm.util.StringUtil;
import com.polaris.conf.admin.Constant;
import com.polaris.conf.admin.core.model.ConfNode;
import com.polaris.conf.admin.core.model.ConfZK;
import com.polaris.conf.admin.core.util.ReturnT;
import com.polaris.conf.admin.dao.ConfNodeDao;
import com.polaris.conf.admin.dao.ConfZKDao;
import com.polaris.config.zk.ConfZkClient;

/**
 * conf zk controller
 */
@Controller
@RequestMapping("/zk")
public class ZKController {
	
	@Autowired
	private ConfZKDao confZKDao;
	@Autowired
	private ConfNodeDao confNodeDao;

	@RequestMapping
	public String index(Model model) {

		List<ConfZK> list = confZKDao.findAll();

		model.addAttribute("list", list);
		return "zk/zk.index";
	}

	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(ConfZK confZK){
		synchronized (this){

			// valid
			if (StringUtil.isEmpty(confZK.getZkName())) {
				return new ReturnT<String>(500, "请输入ZK名称");
			}
			if (confZK.getZkName().length() < 1 || confZK.getZkName().length() > 100) {
				return new ReturnT<String>(500, "ZK名称长度限制为1~100");
			}
			if (StringUtil.isEmpty(confZK.getZkValue())) {
				return new ReturnT<String>(500, "请输入ZK值");
			}
			Constant.ZK_ADDRESS_CONF = confZK.getZkValue();
			ZooKeeper zk = ConfZkClient.getInstance(true);
			if (zk == null || !zk.getState().equals(States.CONNECTED)) {
				ConfZkClient.close();
				return new ReturnT<String>(500, "ZK连接失败");
			}
			ConfZkClient.close();

			// valid repeat
			int count = confZKDao.findCount(confZK.getZkName(), confZK.getZkValue());
			if (count > 0) {
				return new ReturnT<String>(500, "ZK名称或者ZK值已存在,请勿重复添加");
			}

			int ret = confZKDao.save(confZK);
			return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
		}
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(ConfZK confZK){
		synchronized(this) {
			// valid
			if (StringUtil.isEmpty(confZK.getZkName())) {
				return new ReturnT<String>(500, "请输入ZK名称");
			}
			if (confZK.getZkName().length() < 1 || confZK.getZkName().length() > 100) {
				return new ReturnT<String>(500, "ZK名称长度限制为1~100");
			}
			if (StringUtil.isEmpty(confZK.getZkValue())) {
				return new ReturnT<String>(500, "请输入ZK值");
			}
			Constant.ZK_ADDRESS_CONF = confZK.getZkValue();
			ZooKeeper zk = ConfZkClient.getInstance(true);
			if (zk == null || !zk.getState().equals(States.CONNECTED)) {
				ConfZkClient.close();
				return new ReturnT<String>(500, "ZK连接失败");
			}
			ConfZkClient.close();

			// valid repeat
			int count = confZKDao.findCount(confZK.getZkName(), confZK.getZkValue());
			if (count > 1) {
				return new ReturnT<String>(500, "ZK值已存在,请勿重复添加");
			}

			int ret = confZKDao.update(confZK);
			return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
		}
	}

	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(String zkName){

		// valid
		List<ConfNode> confNodeList = confNodeDao.findList(zkName,null, null);
		if (confNodeList.size() > 0) {
			return new ReturnT<String>(500, "该ZK节点使用中, 不可删除");
		}

		int ret = confZKDao.remove(zkName);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

}
