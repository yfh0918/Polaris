package com.polaris.conf.admin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.ZooKeeper.States;
import org.springframework.stereotype.Service;

import com.polaris.comm.util.LogUtil;
import com.polaris.comm.util.StringUtil;
import com.polaris.conf.admin.Constant;
import com.polaris.conf.admin.core.model.ConfGroup;
import com.polaris.conf.admin.core.model.ConfNode;
import com.polaris.conf.admin.core.model.ConfZK;
import com.polaris.conf.admin.core.util.ReturnT;
import com.polaris.conf.admin.dao.ConfGroupDao;
import com.polaris.conf.admin.dao.ConfNodeDao;
import com.polaris.conf.admin.dao.ConfZKDao;
import com.polaris.conf.admin.service.ConfNodeService;
import com.polaris.config.zk.ConfZkClient;

/**
 * 配置
 */
@Service
public class ConfNodeServiceImpl implements ConfNodeService {

    private static LogUtil logger = LogUtil.getInstance(ConfNodeServiceImpl.class);
	@Resource
	private ConfNodeDao confNodeDao;
	@Resource
	private ConfGroupDao confGroupDao;
	@Resource
	private ConfZKDao confZKDao;

	@Override
	public Map<String,Object> findList(String nodeZK, String nodeGroup, String nodeKey) {

		// ConfNode in mysql
		List<ConfNode> data = confNodeDao.findList(nodeZK,nodeGroup, nodeKey);

		// 刷新zookeeper
		ConfZK zkinfo = confZKDao.load(nodeZK);
		if (zkinfo != null && StringUtil.isNotEmpty(zkinfo.getZkValue())) {
			if (!zkinfo.getZkValue().equals(Constant.ZK_ADDRESS_CONF)) {
				Constant.ZK_ADDRESS_CONF = zkinfo.getZkValue();
				ConfZkClient.getInstance(true);
			}
		}

		// fill value in zk
		if (ConfZkClient.getInstance() != null && ConfZkClient.getInstance().getState().equals(States.CONNECTED)) {
			if (CollectionUtils.isNotEmpty(data)) {
				for (ConfNode node: data) {
					String realNodeValue = ConfZkClient.getPathDataByKey(node.getGroupKey());
					node.setNodeValueReal(realNodeValue);
				}
			}
		} else {
			ConfZkClient.close();
		}

		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("data", data);
		maps.put("recordsTotal", data.size());		// 总记录数
		maps.put("recordsFiltered", data.size());	// 过滤后的总记录数
		return maps;

	}

	@Override
	public ReturnT<String> logicDeleteByKey(String nodeZK, String nodeGroup, String nodeKey) {
		if (StringUtils.isBlank(nodeGroup) && StringUtils.isBlank(nodeKey)) {
			return new ReturnT<String>(500, "参数缺失");
		}
		int ret = confNodeDao.logicDeleteByKey(nodeZK,nodeGroup, nodeKey);//zk节点存在，先逻辑删除，最后同步结束之后物理删除
		if (ret < 1) {
			return new ReturnT<String>(500, "Key对应的配置不存在,请确认");
		}
		return ReturnT.SUCCESS;
	}
	
	@Override
	public ReturnT<String> recovery(String nodeZK, String nodeGroup, String nodeKey) {
		if (StringUtils.isBlank(nodeGroup) && StringUtils.isBlank(nodeKey)) {
			return new ReturnT<String>(500, "参数缺失");
		}
		int ret = confNodeDao.recovery(nodeZK,nodeGroup, nodeKey);
		if (ret < 1) {
			return new ReturnT<String>(500, "Key对应的配置不存在,请确认");
		}
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> add(ConfNode confNode) {
		if (confNode == null) {
			return new ReturnT<String>(500, "参数缺失");
		}
		
		//zk节点
		if (StringUtils.isBlank(confNode.getNodeZK())) {
			return new ReturnT<String>(500, "配置ZK节点不可为空");
		}
		ConfZK zk = confZKDao.load(confNode.getNodeZK());
		if (zk==null) {
			return new ReturnT<String>(500, "配置ZK节点不存在");
		}
		
		//应用
		if (StringUtils.isBlank(confNode.getNodeGroup())) {
			return new ReturnT<String>(500, "配置应用不可为空");
		}
		ConfGroup group = confGroupDao.load(confNode.getNodeGroup());
		if (group==null) {
			return new ReturnT<String>(500, "配置应用不存在");
		}
		
		//key
		if (StringUtils.isBlank(confNode.getNodeKey())) {
			return new ReturnT<String>(500, "配置Key不可为空");
		}
		ConfNode existNode = confNodeDao.selectByKey(confNode.getNodeZK(), confNode.getNodeGroup(), confNode.getNodeKey());
		if (existNode!=null) {
			return new ReturnT<String>(500, "Key对应的配置已经存在,不可重复添加");
		}
		confNodeDao.insert(confNode);
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> update(ConfNode confNode) {
		if (confNode == null || StringUtils.isBlank(confNode.getNodeKey())) {
			return new ReturnT<String>(500, "Key不可为空");
		}
		int ret = confNodeDao.update(confNode);
		if (ret < 1) {
			return new ReturnT<String>(500, "Key对应的配置不存在,请确认");
		}
		return ReturnT.SUCCESS;
	}
	
	@Override
	public ReturnT<String> synzk(String nodeZK, String nodeGroup, String nodeKey) {
		// ConfNode in mysql
		List<ConfNode> data = confNodeDao.findList(nodeZK,nodeGroup, nodeKey);

        // 刷新zookeeper
        ConfZK zkinfo = confZKDao.load(nodeZK);
        if (zkinfo != null && StringUtil.isNotEmpty(zkinfo.getZkValue())) {
            if (!zkinfo.getZkValue().equals(Constant.ZK_ADDRESS_CONF)) {
            	Constant.ZK_ADDRESS_CONF = zkinfo.getZkValue();
                ConfZkClient.getInstance(true);
            }
        }
		//同步失败
		if (ConfZkClient.getInstance() == null || !ConfZkClient.getInstance().getState().equals(States.CONNECTED)) {
			ConfZkClient.close();
			return new ReturnT<String>(500, "ZK连接失败");
		}

		// fill value in zk
		if (CollectionUtils.isNotEmpty(data)) {
			for (ConfNode node: data) {
				
				//获取zk节点值
				String realNodeValue = ConfZkClient.getPathDataByKey(node.getGroupKey());
				
				//删除
				if ("1".equals(node.getDelFlg())) {
					ConfZkClient.deletePathByKey(node.getGroupKey());
					confNodeDao.deleteByKey(nodeZK,node.getNodeGroup(), node.getNodeKey());//逻辑删除变成物理删除
                    logger.info("数据从db同步（删除）到zk： nodeZK={}, nodeGroup={}," +
                                    "nodeKey={},nodeValue={}", node.getNodeZK(),
                            node.getNodeGroup(),node.getNodeKey(),node.getNodeValue());
					continue;
				}

				//将value为空的key从ZK中删除
				if (node.getNodeValue() == null)
				{
					ConfZkClient.deletePathByKey(node.getGroupKey());
                    logger.info("数据从db同步（value为空删除）到zk： nodeZK={}, nodeGroup={}," +
                                    "nodeKey={},nodeValue={}", node.getNodeZK(),
                            node.getNodeGroup(),node.getNodeKey(),node.getNodeValue());
					continue;
				}
				
				//更新
				if (realNodeValue != null && !realNodeValue.equals(node.getNodeValue())) {
					ConfZkClient.setPathDataByKey(node.getGroupKey(), node.getNodeValue(), false);
                    logger.info("数据从db同步（更新）到zk： nodeZK={}, nodeGroup={}," +
                                    "nodeKey={},nodeValue={}", node.getNodeZK(),
                            node.getNodeGroup(),node.getNodeKey(),node.getNodeValue());
					continue;
				}
				
				//新增
				if (realNodeValue == null) {
					ConfZkClient.setPathDataByKey(node.getGroupKey(), node.getNodeValue(), false);
                    logger.info("数据从db同步（新增）到zk： nodeZK={}, nodeGroup={}," +
                                    "nodeKey={},nodeValue={}", node.getNodeZK(),
                            node.getNodeGroup(),node.getNodeKey(),node.getNodeValue());
					continue;
				}
				
			}
		}
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<String> synzkAll() {
        List<ConfNode> allNodes = confNodeDao.findList(null, null, null);
        for (ConfNode node : allNodes)
        {
            synzk(node.getNodeZK(),node.getNodeGroup(),node.getNodeKey());
        }
        return ReturnT.SUCCESS;
	}

}
