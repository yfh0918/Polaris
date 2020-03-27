package com.polaris.workflow.service;

import com.polaris.workflow.dto.WorkflowDto;

public interface WorkflowTraceService {

	/**
     * 流程跟踪图
     *
     * @param processInstanceId 流程实例ID
     * @return 封装了各种节点信息
     */
    public WorkflowDto traceProcess(String processInstanceId);

}
