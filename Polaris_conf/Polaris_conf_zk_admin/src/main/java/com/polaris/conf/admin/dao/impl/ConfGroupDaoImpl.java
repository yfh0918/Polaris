package com.polaris.conf.admin.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.polaris.conf.admin.core.model.ConfGroup;
import com.polaris.conf.admin.dao.ConfGroupDao;


@Repository
public class ConfGroupDaoImpl implements ConfGroupDao {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<ConfGroup> findAll() {
        return sqlSessionTemplate.selectList("ConfGroupMapper.findAll");
    }

    @Override
    public int save(ConfGroup confGroup) {
        return sqlSessionTemplate.insert("ConfGroupMapper.save", confGroup);
    }

    @Override
    public int update(ConfGroup confGroup) {
        return sqlSessionTemplate.update("ConfGroupMapper.update", confGroup);
    }

    @Override
    public int remove(String groupName) {
        return sqlSessionTemplate.delete("ConfGroupMapper.remove", groupName);
    }

    @Override
    public ConfGroup load(String groupName) {
        return sqlSessionTemplate.selectOne("ConfGroupMapper.load", groupName);
    }
}
