package com.polaris.demo.service;

import com.polaris.core.dto.ResultDto;
import com.polaris.demo.mapper.ComboBean;

/**
 * @Author: lei.chen@hcit.ai
 * @Description:
 * @CreateTiem: 2019/11/28 15:35
 **/
@SuppressWarnings("rawtypes")
public interface ComboService {

    /**
     * 查询套餐列表
     *
     * @return
     */
	ResultDto findAll();

    /**
     * 查询套餐信息
     *
     * @return
     */
    ComboBean findById(Long comboId);

}
