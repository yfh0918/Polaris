package com.polaris.workflow.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 引擎的全局监听器（监听任务的结束）
 * @author: yufenghua
 */
@Component
public class WorkflowTaskEndListener {

	@Transactional
    public void onEvent(ActivitiEvent event) {
		//nothing
    }
}
