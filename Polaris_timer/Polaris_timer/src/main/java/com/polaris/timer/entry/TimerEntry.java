package com.polaris.timer.entry;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.polaris.comm.dto.ResultDto;
import com.polaris.timer.api.dto.TimerDto;
import com.polaris.timer.api.service.TimerService;
import com.polaris.timer.service.RuleTaskService;

@Service(version = "1.0.0")
public class TimerEntry implements TimerService {
	
	@Autowired
	private RuleTaskService ruleTaskService;

	/**  
	 * @desc  : 
	 * @author: Yang Hao 
	 * @date  : 2017年10月17日 下午2:04:35 
	 * @param dto
	 * @return 
	*/
	@Override
	public List<TimerDto> findActivePlans(TimerDto dto) {
		return ruleTaskService.findActivePlans(dto);
	}

	/**  
	 * @desc  : 更新配置文件
	 * @author: Yang Hao 
	 * @date  : 2017年10月17日 下午2:16:32 
	 * @param dto
	 * @return 
	*/
	@Override
	public ResultDto updateScheduler(TimerDto dto) {
		return ruleTaskService.updateScheduler(dto);
	}
	
}
