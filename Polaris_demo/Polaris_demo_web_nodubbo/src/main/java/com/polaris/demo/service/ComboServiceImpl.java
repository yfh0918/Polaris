package com.polaris.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.polaris.core.datasource.DataSource;
import com.polaris.core.dto.ResultDto;
import com.polaris.core.util.ResultUtil;
import com.polaris.demo.mapper.ComboBean;
import com.polaris.demo.mapper.ComboMapper;

@Service
public class ComboServiceImpl implements ComboService{

	@Autowired
	private ComboMapper comboMapper;
	
	@SuppressWarnings("rawtypes")
	@DataSource("xx")
	@Override
	public ResultDto findAll() {
		return ResultUtil.success(comboMapper.selectAll());
	}

	@DataSource("aa")
	@Override
	public ComboBean findById(Long comboId) {
		// TODO Auto-generated method stub
		return comboMapper.selectByPrimaryKey(comboId);
	}

}
