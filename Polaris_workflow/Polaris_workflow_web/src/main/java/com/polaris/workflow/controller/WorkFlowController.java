package com.polaris.workflow.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.polaris.container.util.RequestUtil;
import com.polaris.workflow.api.dto.WorkflowDto;
import com.polaris.workflow.api.service.WorkflowService;

/**
 * 认证模块
 *
 * @return
 */
@Component
@Path("/api")
public class WorkFlowController {

	@Autowired
	WorkflowService workflowService;
	
    /**
     * 部署单个流程定义
     *
     * @param resourceLoader {@link ResourceLoader}
     * @param processKey     模块名称
     * @throws IOException 找不到zip文件时
     */
    @GET
    @POST
    @Path("/deployDiagram")
    @Produces(MediaType.APPLICATION_JSON)
    public String deployDiagram(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.deployDiagram(dto);
    	return JSON.toJSONString(result);
    }
    

    /**
     * 删除部署的流程，级联删除流程实例
     *
     * @param deploymentId 流程部署ID
     */
    @GET
    @POST
    @Path("/deleteDiagram")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteDiagram(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.deleteDiagram(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 启动流程
     *
     * @param entity
     */
    @GET
    @POST
    @Path("/startWorkflow")
    @Produces(MediaType.APPLICATION_JSON)
    public String startWorkflow(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.startWorkflow(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 查询待办任务
     *
     * @param userId 用户ID
     * @return
     */
    @GET
    @POST
    @Path("/findTodoTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public String findTodoTasks(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.findTodoTasks(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 任务认领
     *
     * @param userId 用户ID
     * @return
     */
    @GET
    @POST
    @Path("/claim")
    @Produces(MediaType.APPLICATION_JSON)
    public String claim(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.claim(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 处理任务
     *
     * @param userId 用户ID
     * @return
     */
    @GET
    @POST
    @Path("/complete")
    @Produces(MediaType.APPLICATION_JSON)
    public String complete(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.complete(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 读取运行中的流程
     *
     * @return
     */
    @GET
    @POST
    @Path("/findRunningProcessInstaces")
    @Produces(MediaType.APPLICATION_JSON)
    public String findRunningProcessInstaces(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.findRunningProcessInstaces(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 读取已结束中的流程
     *
     * @return
     */
    @GET
    @POST
    @Path("/findFinishedProcessInstaces")
    @Produces(MediaType.APPLICATION_JSON)
    public String findFinishedProcessInstaces(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.findFinishedProcessInstaces(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 查询所有使用过的和未使用的businessKey
     *
     * @return
     */
    @GET
    @POST
    @Path("/getBusinessKeyList")
    @Produces(MediaType.APPLICATION_JSON)
    public String getBusinessKeyList(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.getBusinessKeyList(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 删除已经结束的流程
     *
     * @return
     */
    @GET
    @POST
    @Path("/deleteFinishedProcessInstaces")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteFinishedProcessInstaces(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.deleteFinishedProcessInstaces(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 流程跟踪图
     *
     * @param processInstanceId 流程实例ID
     * @return 封装了各种节点信息
     */
    @GET
    @POST
    @Path("/traceProcess")
    @Produces(MediaType.APPLICATION_JSON)
    public String traceProcess(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.traceProcess(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 查询下一个userTask节点
     *
     * @param taskId      任务ID
     * @param gateWayCond 网关判断条件集合
     * @return
     */
    @GET
    @POST
    @Path("/findNextUserTaskName")
    @Produces(MediaType.APPLICATION_JSON)
    public String findNextUserTaskName(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.findNextUserTaskName(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 查询所有userTask节点名称
     *
     * @param processDefinitionId 图ID
     * @return
     */
    @GET
    @POST
    @Path("/getAllUserTask")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllUserTask(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.getAllUserTask(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 查询所有部署的流程
     *
     * @param dto
     * @return
     */
    @GET
    @POST
    @Path("/getAllDeployWorkFlow")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllDeployWorkFlow(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.getAllDeployWorkFlow(dto);
    	return JSON.toJSONString(result);
    }

    /**
     * 删除运行中的流程
     *
     * @param dto
     * @return
     */
    @GET
    @POST
    @Path("/deleteRuntimeProcessInstance")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteRuntimeProcessInstance(@Context HttpServletRequest request) {
    	WorkflowDto dto = RequestUtil.convertParameterToObject(request, WorkflowDto.class);
    	WorkflowDto result = workflowService.deleteRuntimeProcessInstance(dto);
    	return JSON.toJSONString(result);
    }
    


    
}
