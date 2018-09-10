package com.polaris.conf.admin.dao;

import java.util.List;

import com.polaris.conf.admin.core.model.ConfZK;

public interface ConfZKDao {
    public List<ConfZK> findAll();

    public int save(ConfZK confZK);

    public int update(ConfZK confZK);

    public int remove(String zkName);

    public ConfZK load(String zkName);

    public int findCount(String zkName, String zkValue);
}
