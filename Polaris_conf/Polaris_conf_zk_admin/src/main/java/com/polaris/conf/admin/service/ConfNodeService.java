package com.polaris.conf.admin.service;


import java.util.Map;

import com.polaris.conf.admin.core.model.ConfNode;
import com.polaris.conf.admin.core.util.ReturnT;

/**
 * 配置
 */
public interface ConfNodeService {

	public Map<String,Object> findList(String nodeZK, String nodeGroup, String nodeKey);

	public ReturnT<String> logicDeleteByKey(String nodeZK, String nodeGroup, String nodeKey);
	
	public ReturnT<String> recovery(String nodeZK, String nodeGroup, String nodeKey);

	public ReturnT<String> add(ConfNode confNode);

	public ReturnT<String> update(ConfNode confNode);

	public ReturnT<String> synzk(String nodeZK, String nodeGroup, String nodeKey);

	public ReturnT<String> synzkAll();
}
