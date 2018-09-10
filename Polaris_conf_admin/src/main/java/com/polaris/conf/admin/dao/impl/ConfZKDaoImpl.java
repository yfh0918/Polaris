package com.polaris.conf.admin.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.polaris.conf.admin.core.model.ConfZK;
import com.polaris.conf.admin.dao.ConfZKDao;


@Repository
public class ConfZKDaoImpl implements ConfZKDao {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<ConfZK> findAll() {
        return sqlSessionTemplate.selectList("ConfZKMapper.findAll");
    }
    @Override
    public int findCount(String zkName, String zkValue){
    	Map<String, Object> params = new HashMap<String, Object>();
		params.put("zkName", zkName);
		params.put("zkValue", zkValue);
		return sqlSessionTemplate.selectOne("ConfZKMapper.findCount", params);
    }
    @Override
    public int save(ConfZK confZK) {
        return sqlSessionTemplate.insert("ConfZKMapper.save", confZK);
    }

    @Override
    public int update(ConfZK confZK) {
        return sqlSessionTemplate.update("ConfZKMapper.update", confZK);
    }

    @Override
    public int remove(String zkName) {
        return sqlSessionTemplate.delete("ConfZKMapper.remove", zkName);
    }

    @Override
    public ConfZK load(String zkName) {
        return sqlSessionTemplate.selectOne("ConfZKMapper.load", zkName);
    }
}
