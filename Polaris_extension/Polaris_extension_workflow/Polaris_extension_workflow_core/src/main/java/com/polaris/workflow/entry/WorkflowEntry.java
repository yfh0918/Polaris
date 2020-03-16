package com.polaris.workflow.entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.polaris.workflow.api.dto.WorkflowDto;
import com.polaris.workflow.api.service.WorkflowService;
import com.polaris.workflow.service.WorkflowProcessService;
import com.polaris.workflow.service.WorkflowTraceService;

@Service
public class WorkflowEntry implements WorkflowService {

    @Autowired
    private WorkflowProcessService workflowProcessService;

    @Autowired
    private WorkflowTraceService workflowTraceService;

	/**
     * 动态创建流程
     *
     * @param WorkflowDto
     */
	@Override
	public WorkflowDto createDiagram(WorkflowDto dto) {
		return workflowProcessService.createDiagram(dto);
	}
	
    /**
     * 部署单个流程定义
     *
     * @param WorkflowDto
     */
    public WorkflowDto deployDiagram(WorkflowDto dto) {
        return workflowProcessService.deployDiagram(dto);
    }

    /**
     * 删除部署的流程，级联删除流程实例
     *
     * @param deploymentId 流程部署ID
     */
    public WorkflowDto deleteDiagram(WorkflowDto dto) {
        return workflowProcessService.deleteDiagram(dto);
    }

    /**
     * 启动流程
     *
     * @param entity
     */
    public WorkflowDto startWorkflow(WorkflowDto dto) {
        return workflowProcessService.startWorkflow(dto);
    }

    /**
     * 获取流程图
     *
     * @param entity
     */
    public WorkflowDto getProcessDiagram(WorkflowDto dto) {
    	return workflowProcessService.getProcessDiagram(dto);
    }
    
    /**
     * 查询待办任务
     *
     * @param userId 用户ID
     * @return
     */
    public WorkflowDto findTodoTasks(WorkflowDto dto) {
        return workflowProcessService.findTodoTasks(dto);
    }

    /**
     * 任务认领
     *
     * @param userId 用户ID
     * @return
     */
    public WorkflowDto claim(WorkflowDto dto) {
        return workflowProcessService.claim(dto);
    }

    /**
     * 处理任务
     *
     * @param userId 用户ID
     * @return
     */
    public WorkflowDto complete(WorkflowDto dto) {
        return workflowProcessService.complete(dto);
    }

    /**
     * 读取运行中的流程
     *
     * @return
     */
    public WorkflowDto findRunningProcessInstaces(WorkflowDto dto) {
        return workflowProcessService.findRunningProcessInstaces(dto);
    }

    /**
     * 读取已结束中的流程
     *
     * @return
     */
    public WorkflowDto findFinishedProcessInstaces(WorkflowDto dto) {
        return workflowProcessService.findFinishedProcessInstaces(dto);
    }

    /**
     * 查询所有使用过的和未使用的businessKey
     *
     * @return
     */
    public WorkflowDto getBusinessKeyList(WorkflowDto dto) {
        return workflowProcessService.getBusinessKeyList(dto);
    }

    /**
     * 删除已经结束的流程
     *
     * @return
     */
    public WorkflowDto deleteFinishedProcessInstaces(WorkflowDto dto) {
        return workflowProcessService.deleteFinishedProcessInstaces(dto);
    }

    /**
     * 流程跟踪图
     *
     * @param processInstanceId 流程实例ID
     * @return 封装了各种节点信息
     */
    public WorkflowDto traceProcess(WorkflowDto dto) {
        return workflowTraceService.traceProcess(dto);
    }

    /**
     * 查询下一个userTask节点
     *
     * @param taskId      任务ID
     * @param gateWayCond 网关判断条件集合
     * @return
     */
    @Override
    public WorkflowDto findNextUserTaskName(WorkflowDto dto) {
        return workflowProcessService.getNextTaskGroup(dto);
    }

    /**
     * 查询所有userTask节点名称
     *
     * @param processDefinitionId 图ID
     * @return
     */
    @Override
    public WorkflowDto getAllUserTask(WorkflowDto dto) {
        return workflowProcessService.getAllUserTask(dto);
    }

    /**
     * 查询所有部署的流程
     *
     * @param dto
     * @return
     */
    @Override
    public WorkflowDto getAllDeployWorkFlow(WorkflowDto dto) {
        return workflowProcessService.getAllDeployWorkFlow(dto);
    }

    /**
     * 删除运行中的流程
     *
     * @param dto
     * @return
     */
    @Override
    public WorkflowDto deleteRuntimeProcessInstance(WorkflowDto dto) {
        return workflowProcessService.deleteRuntimeProcessInstance(dto);
    }

}
