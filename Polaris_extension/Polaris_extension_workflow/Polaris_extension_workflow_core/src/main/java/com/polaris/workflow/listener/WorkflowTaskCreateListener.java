package com.polaris.workflow.listener;

import java.util.List;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.polaris.workflow.dto.WorkflowDto;

/**
 * 引擎的全局监听器（监听任务的创建）
 * @author: yufenghua
 */
@Component
public class WorkflowTaskCreateListener {

    @SuppressWarnings("unchecked")
    @Transactional
    public void onEvent(ActivitiEvent event) {
    	ActivitiEntityEvent entityEvent = (ActivitiEntityEvent) event;
        TaskEntity taskEntity = (TaskEntity) entityEvent.getEntity();
        
		//没有参数直接返回
		if (taskEntity.getVariables() == null) {
			return;
		}
		
		//判断assignee
		if (taskEntity.getVariable(WorkflowDto.ASSIGNEE) != null) {
			taskEntity.setAssignee(taskEntity.getVariable(WorkflowDto.ASSIGNEE).toString());
		}
		
		//判断users
		if (taskEntity.getVariable(WorkflowDto.CANDIDATE_USERS) != null) {
			taskEntity.addCandidateUsers((List<String>)taskEntity.getVariable(WorkflowDto.CANDIDATE_USERS));
		}
		
		//判断groups
		if (taskEntity.getVariable(WorkflowDto.CANDIDATE_GROUPS) != null) {
			taskEntity.addCandidateGroups((List<String>)taskEntity.getVariable(WorkflowDto.CANDIDATE_GROUPS));
		}

    }

}
