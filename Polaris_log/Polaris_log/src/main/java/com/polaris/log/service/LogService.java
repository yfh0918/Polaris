package com.polaris.log.service;

import java.util.List;

import com.polaris.log.api.dto.LogDto;

public interface LogService {
	
	//日志插入
	public void insert(LogDto logDto) ;
	
	//日志插入
	public void insert(List<LogDto> logDtos) ;

	//日志查询
	public List<LogDto> query(LogDto dto);
}
	
