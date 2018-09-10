package com.polaris.timer.api.service;

import java.util.List;

import com.polaris.comm.dto.ResultDto;
import com.polaris.timer.api.dto.TimerDto;

public interface TimerService {

	//获取所有的计划任务
	public List<TimerDto> findActivePlans(TimerDto dto);

	//新增，更新，删除计划任务
	public ResultDto updateScheduler(TimerDto dto);
}
