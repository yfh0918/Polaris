package com.polaris.workflow.api.service;

import java.io.IOException;

import org.springframework.core.io.ResourceLoader;

import com.polaris.workflow.api.dto.WorkflowDto;

public interface WorkflowService {

    /**
     * 部署单个流程定义
     *
     * @param resourceLoader {@link ResourceLoader}
     * @param processKey     模块名称
     * @throws IOException 找不到zip文件时
     */
    public WorkflowDto deployDiagram(WorkflowDto dto);

    /**
     * 删除部署的流程，级联删除流程实例
     *
     * @param deploymentId 流程部署ID
     */
    public WorkflowDto deleteDiagram(WorkflowDto dto);

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
     * 查询所有使用过的和未使用的businessKey
     *
     * @return
     */
    public WorkflowDto getBusinessKeyList(WorkflowDto dto);

    /**
     * 删除已经结束的流程
     *
     * @return
     */
    public WorkflowDto deleteFinishedProcessInstaces(WorkflowDto dto);

    /**
     * 流程跟踪图
     *
     * @param processInstanceId 流程实例ID
     * @return 封装了各种节点信息
     */
    public WorkflowDto traceProcess(WorkflowDto dto);

    /**
     * 查询下一个userTask节点
     *
     * @param taskId      任务ID
     * @param gateWayCond 网关判断条件集合
     * @return
     */
    WorkflowDto findNextUserTaskName(WorkflowDto dto);

    /**
     * 查询所有userTask节点名称
     *
     * @param processDefinitionId 图ID
     * @return
     */
    WorkflowDto getAllUserTask(WorkflowDto dto);

    /**
     * 查询所有部署的流程
     *
     * @param dto
     * @return
     */
    WorkflowDto getAllDeployWorkFlow(WorkflowDto dto);

    /**
     * 删除运行中的流程
     *
     * @param dto
     * @return
     */
    WorkflowDto deleteRuntimeProcessInstance(WorkflowDto dto);
}
