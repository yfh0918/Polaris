package com.polaris.workflow.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiVariableEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 引擎的全局监听器（监听变量的创建）
 *
 * @author: yufenghua
 */
@Component
public class WorkflowVariableCreateListener {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    public void onEvent(ActivitiEvent event) {
        ActivitiVariableEvent variableEvent = (ActivitiVariableEvent) event;
        logger.debug("创建了变量: " + variableEvent.getVariableName() + ", 值：" + variableEvent.getVariableValue());
    }

}
