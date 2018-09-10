package com.polaris.workflow.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.polaris.comm.util.LogUtil;

@Component
public class WorkflowListener implements ActivitiEventListener {
    protected LogUtil logger = LogUtil.getInstance(getClass());

    @Autowired
    private WorkflowVariableCreateListener workflowVariableCreateListener;//变量创建

    @Autowired
    private WorkflowTaskCreateListener workflowTaskCreateListener;//任务创建

    @Autowired
    private WorkflowTaskEndListener workflowTaskEndListener;//任务结束

    @Autowired
    private WorkflowProcessEndListener workflowProcessEndListener;//流程结束

    @Override
    @Transactional
    public void onEvent(ActivitiEvent event) {
        switch (event.getType()) {

            //变量创建
            case VARIABLE_CREATED:
                workflowVariableCreateListener.onEvent(event);
                break;

            //任务创建
            case TASK_CREATED:
                workflowTaskCreateListener.onEvent(event);
                break;

            //任务结束
            case TASK_COMPLETED:
                workflowTaskEndListener.onEvent(event);
                break;

            //流程结束
            case PROCESS_COMPLETED:
                workflowProcessEndListener.onEvent(event);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
