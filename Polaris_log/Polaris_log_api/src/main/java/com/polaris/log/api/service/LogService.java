package com.polaris.log.api.service;

import java.util.List;

import com.polaris.log.api.dto.LogDto;

public interface LogService {

	//插入日志
	public void insert(LogDto logDto);
	
	//日志查询
	public List<LogDto> query(LogDto dto);
}
