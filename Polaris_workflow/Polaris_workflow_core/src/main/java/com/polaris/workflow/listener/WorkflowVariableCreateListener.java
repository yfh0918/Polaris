package com.polaris.workflow.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiVariableEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.polaris.comm.util.LogUtil;

/**
 * 引擎的全局监听器（监听变量的创建）
 *
 * @author: yufenghua
 */
@Component
public class WorkflowVariableCreateListener {

    protected LogUtil logger = LogUtil.getInstance(getClass());

    @Transactional
    public void onEvent(ActivitiEvent event) {
        ActivitiVariableEvent variableEvent = (ActivitiVariableEvent) event;
        logger.debug("创建了变量: " + variableEvent.getVariableName() + ", 值：" + variableEvent.getVariableValue());
    }

}
