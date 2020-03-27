package com.polaris.workflow.service;

import com.polaris.workflow.dto.WorkflowDto;

public interface WorkflowProcessService {
	
    /**
     * 启动流程
     *
     * @param entity
     */
    public WorkflowDto startWorkflow(WorkflowDto dto);

    /**
     * 查询待办任务
     *
     * @param userId 用户ID
     * @return
     */
    public WorkflowDto findTodoTasks(WorkflowDto dto);

    /**
     * 任务认领
     *
     * @param userId 用户ID
     * @return
     */
    
    public WorkflowDto claim(WorkflowDto dto);

    /**
     * 处理任务
     *
     * @param userId 用户ID
     * @return
     */
    
    public WorkflowDto complete(WorkflowDto dto);

    /**
     * 读取运行中的流程
     *
     * @return
     */
    public WorkflowDto findRunningProcessInstaces(WorkflowDto dto);

    /**
     * 读取已结束中的流程
     *
     * @return
     */
    public WorkflowDto findFinishedProcessInstaces(WorkflowDto dto);
    

    /**
     * 删除没有结束的流程
     *
     * @return
     */
    
    public WorkflowDto deleteRuntimeProcessInstance(String processInstanceId, String deleteReason);

    /**
     * 删除已经结束的流程
     *
     * @return
     */
    
    public WorkflowDto deleteFinishedProcessInstaces(String processInstanceId);

    /**
     * 查询所有使用过的和未使用的businessKey
     *
     * @return
     */
    public WorkflowDto getBusinessKeyList(WorkflowDto dto);

    /**
     * 获取下一个用户任务用户组信息
     *
     * @param taskId      任务Id信息
     * @param gateWayCond 工作流的网关判断条件
     * @return 下一个用户任务用户组信息
     * @throws Exception
     */
	public WorkflowDto getNextTaskGroup(WorkflowDto dto);

    /**
     * 获取所有的用户节点名称
     *
     * @param processDefinitionId 图Id
     * @return 所有用户任务用户名称
     * @throws Exception
     */
	public WorkflowDto getAllUserTask(WorkflowDto dto);
}
