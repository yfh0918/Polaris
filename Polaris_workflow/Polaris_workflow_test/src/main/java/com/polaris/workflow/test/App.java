package com.polaris.workflow.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.polaris.core.util.SpringUtil;
import com.polaris.core.util.UuidUtil;
import com.polaris.workflow.api.dto.WorkflowDto;
import com.polaris.workflow.api.service.WorkflowService;

/**
 * Hello world!
 *
 */
@Component
public class App 
{
	@Reference(version="1.0.0")
    private WorkflowService workflowService;

    public static void main( String[] args )
    {
    	App app = SpringUtil.getBean(App.class);
//    	app.deployDiagram();
//    	app.startWorkflow();//启动流程
//    	app.findTodoTasks();//查询自己的任务列表
//    	app.claim();//认领
//    	app.complete();//完成任务
    	app.findRunningProcessInstaces();//查询运行的任务
    	app.findFinishedProcessInstaces();//查询完成的任务 	
//    	app.deleteFinishedProcessInstaces();
    }
    
    /**
     * 部署单个流程定义
     *
     * @param resourceLoader {@link ResourceLoader}
     * @param processKey     模块名称
     * @throws IOException 找不到zip文件时
     */
    public void deployDiagram() {
 	   	WorkflowDto dto = new WorkflowDto();
  		dto.setProcessDefinitionKey("wgshxx");
  		dto = workflowService.deployDiagram(dto);
  		System.out.println((JSONObject) JSONObject.toJSON(dto));
    }
    
    /**
     * 删除部署的流程，级联删除流程实例
     *
     * @param deploymentId 流程部署ID
     */
   public void deleteDiagram() {
	   WorkflowDto dto = new WorkflowDto();
   		dto.setDeploymentId("3d8dfd24-7bd7-11e7-8ac4-507b9dc4ab39");
   		dto = workflowService.deleteDiagram(dto);
    	System.out.println((JSONObject) JSONObject.toJSON(dto));
   }
    
    /**
     * 启动流程
     *
     * @param entity
     */
    public void startWorkflow() {
    	WorkflowDto dto = new WorkflowDto();
    	dto.setUserId("530");
    	dto.setBusinessKey(UuidUtil.generateUuid());
    	dto.setProcessDefinitionKey("wgshxx");
    	
    	List<String> groups = new ArrayList<>();
    	groups.add("185");
    	dto.setCandidateGroups(groups);
//    	dto.setAssignee("468");

//    	dto.setAssignee("checker001");
    	dto = workflowService.startWorkflow(dto);
    	System.out.println((JSONObject) JSONObject.toJSON(dto));
    }
    
    /**
     * 查询待办任务
     *
     * @param userId 用户ID
     * @return
     */
    public void findTodoTasks(){
    	WorkflowDto dto = new WorkflowDto();
    	dto.setUserId("308");
    	dto.setProcessDefinitionKey("wgshxx");
    	dto.setPageIndex(1);
    	dto.setPageSize(10);
    	dto = workflowService.findTodoTasks(dto);
    	System.out.println((JSONObject) JSONObject.toJSON(dto));    	
    }
    
   
    /**
     * 任务认领
     *
     * @param userId 用户ID
     * @return
     */
    public void claim() {
    	WorkflowDto dto = new WorkflowDto();
    	
    	//谁来认领任务
    	dto.setUserId("423");

    	//指定任务ID（从findTodoTasks获取）
    	String taskId = "3b2e6363-80d3-11e7-88f7-000c295e0f9b";

    	dto.setTaskId(taskId);
    	
    	//执行任务认领
    	dto = workflowService.claim(dto);
    	System.out.println((JSONObject) JSONObject.toJSON(dto));
    }
 
    /**
     * 处理任务
     *
     * @param userId 用户ID
     * @return
     */
    public void complete(){
    	WorkflowDto dto = new WorkflowDto();
    	
    	//指定任务ID（从findTodoTasks获取）
    	String taskId = "3b2e6363-80d3-11e7-88f7-000c295e0f9b";
    	dto.setTaskId(taskId);
    	
    	//设置判断参数为OK
    	dto.setOk(true);
    	dto.setComment("觉得没有任何问题，可以通过！");

    	//指派给checker002
    	List<String> users = new ArrayList<>();
    	users.add("423");
    	users.add("445");
    	dto.setCandidateUsers(users);
    	
    	//执行处理
    	dto = workflowService.complete(dto);
    	System.out.println((JSONObject) JSONObject.toJSON(dto));

    }
 

    /**
     * 读取运行中的流程
     *
     * @return
     */
    public void findRunningProcessInstaces(){
    	WorkflowDto dto = new WorkflowDto();
    	dto.setProcessDefinitionKey("wgshxx");
    	dto.setPageIndex(1);
    	dto.setPageSize(10);
    	dto = workflowService.findRunningProcessInstaces(dto);
    	System.out.println((JSONObject) JSONObject.toJSON(dto));    	
    }
    
    /**
     * 读取已结束中的流程
     *
     * @return
     */
    public void findFinishedProcessInstaces(){
    	WorkflowDto dto = new WorkflowDto();
    	dto.setProcessDefinitionKey("wgshxx");
    	dto.setPageIndex(1);
    	dto.setPageSize(10);
    	dto = workflowService.findFinishedProcessInstaces(dto);
    	System.out.println((JSONObject) JSONObject.toJSON(dto));  
    }
    
    /**
     * 删除已经结束的流程
     *
     * @return
     */
    public void deleteFinishedProcessInstaces(){
    	WorkflowDto dto = new WorkflowDto();
    	dto.setProcessInstanceId("bccbf734-80bc-11e7-af93-000c295e0f9b");
    	
     	//执行处理
    	dto = workflowService.deleteFinishedProcessInstaces(dto);
    	System.out.println((JSONObject) JSONObject.toJSON(dto));
    }
    
    /**
     * 流程跟踪图
     *
     * @param processInstanceId 流程实例ID
     * @return 封装了各种节点信息
     */
    public void traceProcess(){
    	
    }
}
