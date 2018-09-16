package com.polaris.conf.admin.dao;


import java.util.List;

import com.polaris.conf.admin.core.model.ConfNode;


/**
 * 配置
 */
public interface ConfNodeDao {

	public List<ConfNode> findList(String nodeZK, String nodeGroup, String nodeKey);

	public int deleteByKey(String nodeZK, String nodeGroup, String nodeKey);
	
	public int logicDeleteByKey(String nodeZK, String nodeGroup, String nodeKey);
	
	public int recovery(String nodeZK, String nodeGroup, String nodeKey);
	

	public void insert(ConfNode confNode);

	public ConfNode selectByKey(String nodeZK, String nodeGroup, String nodeKey);

	public int update(ConfNode confNode);
	
}
