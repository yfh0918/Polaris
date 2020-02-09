package com.polaris.extension.db;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 继承通用mapper的增上改查，进行扩张
 *
 * @param <T>
 * @author jacky
 */
public interface PolarisBaseMapper<T> extends Mapper<T>, MySqlMapper<T> {
}