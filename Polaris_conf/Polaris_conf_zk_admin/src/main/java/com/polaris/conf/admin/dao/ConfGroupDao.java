package com.polaris.conf.admin.dao;

import java.util.List;

import com.polaris.conf.admin.core.model.ConfGroup;

public interface ConfGroupDao {
    public List<ConfGroup> findAll();

    public int save(ConfGroup confGroup);

    public int update(ConfGroup confGroup);

    public int remove(String groupName);

    public ConfGroup load(String groupName);
}
