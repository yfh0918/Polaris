package com.polaris.timer.service;

import java.util.List;

import com.polaris.comm.dto.ResultDto;
import com.polaris.timer.api.dto.TimerDto;

public interface RuleTaskService {
	/**   
	 * @desc  : 查询计划任务
	 * @author: Yang Hao
	 * @date  : 2017年10月17日 下午1:56:24
	 * @param dto
	 * @return
	*/
	List<TimerDto> findActivePlans(TimerDto dto);
	
	/**   
	 * @desc  : 新增，更新，删除计划任务
	 * @author: Yang Hao
	 * @date  : 2017年10月17日 下午2:14:40
	 * @param dto
	 * @return
	*/
	ResultDto updateScheduler(TimerDto dto);
}
