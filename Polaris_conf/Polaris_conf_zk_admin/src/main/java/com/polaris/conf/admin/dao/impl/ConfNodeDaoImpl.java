package com.polaris.conf.admin.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.polaris.conf.admin.core.model.ConfNode;
import com.polaris.conf.admin.dao.ConfNodeDao;

/**
 * 配置
 */

@Repository
public class ConfNodeDaoImpl implements ConfNodeDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public List<ConfNode> findList(String nodeZK, String nodeGroup, String nodeKey) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nodeZK", nodeZK);
		params.put("nodeGroup", nodeGroup);
		params.put("nodeKey", nodeKey);

		return sqlSessionTemplate.selectList("ConfNodeMapper.findList", params);
	}

	@Override
	public int deleteByKey(String nodeZK, String nodeGroup, String nodeKey) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nodeZK", nodeZK);
		params.put("nodeGroup", nodeGroup);
		params.put("nodeKey", nodeKey);

		return sqlSessionTemplate.delete("ConfNodeMapper.deleteByKey", params);
	}
	
	@Override
	public int logicDeleteByKey(String nodeZK, String nodeGroup, String nodeKey) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nodeZK", nodeZK);
		params.put("nodeGroup", nodeGroup);
		params.put("nodeKey", nodeKey);

		return sqlSessionTemplate.update("ConfNodeMapper.logicDeleteByKey", params);
	}
	
	@Override
	public int recovery(String nodeZK, String nodeGroup, String nodeKey) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nodeZK", nodeZK);
		params.put("nodeGroup", nodeGroup);
		params.put("nodeKey", nodeKey);

		return sqlSessionTemplate.update("ConfNodeMapper.recovery", params);
	}

	@Override
	public void insert(ConfNode node) {
		sqlSessionTemplate.insert("ConfNodeMapper.insert", node);
	}

	@Override
	public ConfNode selectByKey(String nodeZK, String nodeGroup, String nodeKey) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nodeZK", nodeZK);
		params.put("nodeGroup", nodeGroup);
		params.put("nodeKey", nodeKey);

		return sqlSessionTemplate.selectOne("ConfNodeMapper.selectByKey", params);
	}

	@Override
	public int update(ConfNode node) {
		return sqlSessionTemplate.update("ConfNodeMapper.update", node);
	}
	
}
