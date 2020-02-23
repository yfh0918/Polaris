package com.polaris.workflow.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.polaris.container.config.ConfigurationSupport;
import com.polaris.core.Constant;
import com.polaris.core.config.ConfHandlerProvider;
import com.polaris.core.util.SpringUtil;
import com.polaris.workflow.api.dto.WorkflowDto;
import com.polaris.workflow.api.service.WorkflowService;

@Component
public class ClaimApp {
    @Reference
    private WorkflowService workflowService;

    private final String processDefinitionKey = "claim";
    private final String businessKey = "test002";

    public static void main(String[] args) {
    	ConfHandlerProvider.INSTANCE.init();
        SpringUtil.refresh(ConfigurationSupport.getConfiguration());
        ClaimApp app = SpringUtil.getBean(ClaimApp.class);
        app.deployDiagram();//载入流程（即使重复调用，也不会重复载入）
//    	app.startWorkflow();//启动流程
//    	app.findTodoTasks();//查询自己的任务列表
//    	app.claim();//认领
//    	app.complete();//完成任务
//    	app.findRunningProcessInstaces();//查询运行的任务
//    	app.findFinishedProcessInstaces();//查询完成的任务 	
//    	app.deleteFinishedProcessInstaces();//删除结束的流程
//    	app.traceProcess();//流程跟踪
//    	app.getBusinessKeyList();

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
        //dto.setProcessDefinitionKey("wgshxx");
        // dto = workflowService.deployDiagram(dto);
        dto = workflowService.getAllDeployWorkFlow(dto);
        System.out.println((JSONObject) JSONObject.toJSON(dto));
        //{"pageSize":10,"pageTo":10,"processDefinitionKey":"claim","total":0,"deploymentId":"b65da5bf-7d6c-11e7-9afd-000c295e0f9b","map":{},"totalPage":0,"pageFrom":1,"pageIndex":1,"status":0}

    }

    /**
     * 启动流程
     *
     * @param entity
     */
    public void startWorkflow() {
        WorkflowDto dto = new WorkflowDto();
        dto.setUserId("530");//流程发起人
        dto.setBusinessKey(businessKey);//案件编号（保证唯一性）
        dto.setProcessDefinitionKey(processDefinitionKey);//流程名称，固定(claim)

        //指定下一个节点谁来处理（单个用户可以用setAssignee）
        //复数用户（setCandidateUsers），复数角色（setCandidateGroups）
//    	dto.setAssignee("checker001");
        List<String> groups = new ArrayList<>();
        groups.add("185");
        dto.setCandidateGroups(groups);
        dto = workflowService.startWorkflow(dto);//启动流程
        System.out.println((JSONObject) JSONObject.toJSON(dto));//查看返回结果，目前是json
        //{"pageSize":10,"pageTo":10,"processDefinitionKey":"claim","total":0,"map":{},"processInstanceId":"b6eee943-7d6c-11e7-9afd-000c295e0f9b","variables":{"assignee":"checker001"},"totalPage":0,"pageFrom":1,"userId":"yufenghua","pageIndex":1,"businessKey":"test001","status":0}
    }

    /**
     * 查询待办任务
     *
     * @param userId 用户ID
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void findTodoTasks() {
        WorkflowDto dto = new WorkflowDto();
        dto.setUserId("310");//可以查看checker001需要处理的案件
        dto.setProcessDefinitionKey(processDefinitionKey);//流程名称，固定(claim)
        dto.setPageIndex(1);//分页用，开始页
        dto.setPageSize(10);//分页用，一页最大显示条数
        dto = workflowService.findTodoTasks(dto);//查看需要处理的案件

        //判断成功
        if (dto.getCode().equals(Constant.RESULT_SUCCESS)) {
            if (dto.getData() != null) {
            	List<Map<String, Object>> datas = (List)dto.getData();
                for (Map<String, Object> dataMap : datas) {
                    dataMap.get(WorkflowDto.BUSINESS_KEY);
                    dataMap.get(WorkflowDto.PROCESS_INSTANCE_ID);
                    dataMap.get(WorkflowDto.PROCESS_DEFINITION_ID);
                    dataMap.get(WorkflowDto.APPLY_USERID);//谁发起的
                    dataMap.get(WorkflowDto.TASK_ID);
                    dataMap.get(WorkflowDto.TASK_NAME);
                    dataMap.get(WorkflowDto.TASK_ASSIGNEE);
                    Map<String, Object> variables = (Map<String, Object>) dataMap.get(WorkflowDto.TASK_VARIABLES);
                    variables.get(WorkflowDto.COMMENT);//节点的留言
                    variables.get(WorkflowDto.ASSIGNEE);//string(上一个节点发给谁的任务)
                    variables.get(WorkflowDto.CANDIDATE_USERS);//list(上一个节点发给谁的任务)
                    variables.get(WorkflowDto.CANDIDATE_GROUPS);//list(上一个节点发给谁的任务)role
                }
            }
        }
        //	String applyUserId = dto.getDatas().get(0).get(WorkflowDto.APPLY_USERID).toString();//获取任务发起人
        System.out.println((JSONObject) JSONObject.toJSON(dto));
        //{"pageSize":10,"pageTo":10,"processDefinitionKey":"claim","total":1,"map":{},"datas":[{"businessKey":"test001","processInstanceId":"b6eee943-7d6c-11e7-9afd-000c295e0f9b","processDefinitionId":"claim:1:b68d4142-7d6c-11e7-9afd-000c295e0f9b","taskName":"理赔单据立案","taskVariables":{"applyUserId":"yufenghua","assignee":"checker001"},"taskId":"b6f3a439-7d6c-11e7-9afd-000c295e0f9b"}],"totalPage":1,"pageFrom":1,"userId":"checker001","pageIndex":1,"status":0}
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
        dto.setUserId("443");

        //指定任务ID（从findTodoTasks获取）
        String taskId = "ee4101c8-816f-11e7-bd67-000c295e0f9b";
        dto.setTaskId(taskId);

        //执行任务认领
        dto = workflowService.claim(dto);
        System.out.println((JSONObject) JSONObject.toJSON(dto));
        //{"pageSize":10,"pageTo":10,"total":0,"map":{},"totalPage":0,"pageFrom":1,"userId":"checker001","pageIndex":1,"taskId":"b6f3a439-7d6c-11e7-9afd-000c295e0f9b","status":0}
    }

    /**
     * 处理任务
     *
     * @param userId 用户ID
     * @return
     */
    public void complete() {
        WorkflowDto dto = new WorkflowDto();

        //指定任务ID（从findTodoTasks获取）
        String taskId = "ee4101c8-816f-11e7-bd67-000c295e0f9b";
        dto.setTaskId(taskId);

        //指定下一个节点谁来处理（单个用户可以用setAssignee）
        //复数用户（setCandidateUsers），复数角色（setCandidateGroups）
        List<String> users = new ArrayList<>();
        users.add("310");
        users.add("313");
        dto.setCandidateUsers(users);
        dto.setComment("please check");
        dto.setOk(true);
        dto.setType(0);

        //执行处理
        dto = workflowService.complete(dto);
        System.out.println((JSONObject) JSONObject.toJSON(dto));
//    	{"pageSize":10,"pageTo":10,"total":0,"map":{},"variables":{"assignee":"checker002"},"totalPage":0,"pageFrom":1,"pageIndex":1,"taskId":"b6f3a439-7d6c-11e7-9afd-000c295e0f9b","status":0}
    }

    /**
     * 读取运行中的流程
     *
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void findRunningProcessInstaces() {
        WorkflowDto dto = new WorkflowDto();
        dto.setProcessDefinitionKey(processDefinitionKey);
        dto.setPageIndex(1);
        dto.setPageSize(10);
        dto = workflowService.findRunningProcessInstaces(dto);

        //判断成功
        if (dto.getCode().equals(Constant.RESULT_SUCCESS)) {
            if (dto.getData() != null) {
            	List<Map<String, Object>> datas = (List)dto.getData();
                for (Map<String, Object> dataMap : datas) {
                    dataMap.get(WorkflowDto.BUSINESS_KEY);
                    dataMap.get(WorkflowDto.PROCESS_INSTANCE_ID);
                    dataMap.get(WorkflowDto.PROCESS_DEFINITION_ID);
                    dataMap.get(WorkflowDto.APPLY_USERID);

                    //具体的task
                    if (dataMap.get(WorkflowDto.TASKS) != null) {
                        List<Map<String, Object>> tasks = (List<Map<String, Object>>) dataMap.get(WorkflowDto.TASKS);
                        for (Map<String, Object> task : tasks) {
                            task.get(WorkflowDto.TASK_ID);
                            task.get(WorkflowDto.TASK_NAME);
                            task.get(WorkflowDto.TASK_ASSIGNEE);
                            Map<String, Object> variables = (Map<String, Object>) task.get(WorkflowDto.TASK_VARIABLES);
                            variables.get(WorkflowDto.COMMENT);//节点的留言
                            variables.get(WorkflowDto.ASSIGNEE);//string(上一个节点发给谁的任务)
                            variables.get(WorkflowDto.CANDIDATE_USERS);//list(上一个节点发给谁的任务)
                            variables.get(WorkflowDto.CANDIDATE_GROUPS);//list(上一个节点发给谁的任务)role

                        }
                    }
                }
            }
        }


        System.out.println((JSONObject) JSONObject.toJSON(dto));
//    	{"pageSize":10,"pageTo":10,"processDefinitionKey":"claim","total":1,"map":{},"datas":[{"businessKey":"test001","processInstanceId":"b6eee943-7d6c-11e7-9afd-000c295e0f9b","processDefinitionId":"claim:1:b68d4142-7d6c-11e7-9afd-000c295e0f9b","taskName":"立案查询","taskVariables":{"applyUserId":"yufenghua","assignee":"checker002"},"taskId":"7ccbeadc-7d6f-11e7-9afd-000c295e0f9b"}],"totalPage":1,"pageFrom":1,"pageIndex":1,"status":0}
    }

    /**
     * 读取已结束中的流程
     *
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void findFinishedProcessInstaces() {
        WorkflowDto dto = new WorkflowDto();
        dto.setProcessDefinitionKey(processDefinitionKey);
        dto.setPageIndex(1);
        dto.setPageSize(10);
        dto = workflowService.findFinishedProcessInstaces(dto);

        //判断成功
        if (dto.getCode().equals(Constant.RESULT_SUCCESS)) {
            if (dto.getData() != null) {
            	List<Map<String, Object>> datas = (List)dto.getData();
                for (Map<String, Object> dataMap : datas) {
                    dataMap.get(WorkflowDto.BUSINESS_KEY);
                    dataMap.get(WorkflowDto.PROCESS_INSTANCE_ID);
                    dataMap.get(WorkflowDto.PROCESS_DEFINITION_ID);
                    dataMap.get(WorkflowDto.APPLY_USERID);

                    //具体的task
                    if (dataMap.get(WorkflowDto.TASKS) != null) {
                        List<Map<String, Object>> tasks = (List<Map<String, Object>>) dataMap.get(WorkflowDto.TASKS);
                        for (Map<String, Object> task : tasks) {
                            task.get(WorkflowDto.TASK_ID);
                            task.get(WorkflowDto.TASK_NAME);
                            task.get(WorkflowDto.TASK_ASSIGNEE);
                            Map<String, Object> variables = (Map<String, Object>) task.get(WorkflowDto.TASK_VARIABLES);
                            variables.get(WorkflowDto.COMMENT);//节点的留言
                            variables.get(WorkflowDto.ASSIGNEE);//string(上一个节点发给谁的任务)
                            variables.get(WorkflowDto.CANDIDATE_USERS);//list(上一个节点发给谁的任务)
                            variables.get(WorkflowDto.CANDIDATE_GROUPS);//list(上一个节点发给谁的任务)role

                        }
                    }
                }
            }
        }
        System.out.println((JSONObject) JSONObject.toJSON(dto));
//    	{"pageSize":10,"pageTo":10,"processDefinitionKey":"claim","total":1,"map":{},"datas":[{"businessKey":"test001","processInstanceId":"b6eee943-7d6c-11e7-9afd-000c295e0f9b","processDefinitionId":"claim:1:b68d4142-7d6c-11e7-9afd-000c295e0f9b"}],"totalPage":1,"pageFrom":1,"pageIndex":1,"status":0}
    }

    /**
     * 读取已结束中的流程
     *
     * @return
     */
    public void getBusinessKeyList() {
        WorkflowDto dto = new WorkflowDto();
        dto.setProcessDefinitionKey(processDefinitionKey);
        dto.setPageIndex(1);
        dto.setPageSize(10);
        dto = workflowService.getBusinessKeyList(dto);

        //判断成功
        if (dto.getCode().equals(Constant.RESULT_SUCCESS)) {
            dto.getUsedBusinessKeyList();
            dto.getUsingBusinessKeyList();
        }
        System.out.println((JSONObject) JSONObject.toJSON(dto));
//    	{"pageSize":10,"pageTo":10,"processDefinitionKey":"claim","total":1,"map":{},"datas":[{"businessKey":"test001","processInstanceId":"b6eee943-7d6c-11e7-9afd-000c295e0f9b","processDefinitionId":"claim:1:b68d4142-7d6c-11e7-9afd-000c295e0f9b"}],"totalPage":1,"pageFrom":1,"pageIndex":1,"status":0}
    }

    /**
     * 流程跟踪图
     *
     * @param processInstanceId 流程实例ID
     * @return 封装了各种节点信息
     */
    public void traceProcess() {
        WorkflowDto dto = new WorkflowDto();
        dto.setProcessInstanceId("b6eee943-7d6c-11e7-9afd-000c295e0f9b");
        dto = workflowService.traceProcess(dto);
        System.out.println((JSONObject) JSONObject.toJSON(dto));
        // {"pageSize":10,"pageTo":10,"total":0,"map":{},"processInstanceId":"b6eee943-7d6c-11e7-9afd-000c295e0f9b","totalPage":0,"pageFrom":1,"msgContent":"没有processInstanceId","pageIndex":1,"status":1}
    }

    /**
     * 删除已经结束的流程
     *
     * @return
     */
    public void deleteFinishedProcessInstaces() {
        WorkflowDto dto = new WorkflowDto();
        dto.setProcessInstanceId("5af8c2cd-7cb0-11e7-b0e2-000c295e0f9b");

        //执行处理
        dto = workflowService.deleteFinishedProcessInstaces(dto);
        System.out.println((JSONObject) JSONObject.toJSON(dto));
    }

    /**
     * 删除运行中的流程
     */
    public void deleteRuntimeProcessInstance() {
        WorkflowDto dto = new WorkflowDto();
        dto.setProcessInstanceId("5af8c2cd-7cb0-11e7-b0e2-000c295e0f9b");
        dto.setDeleteReason("delete reason");
        dto = workflowService.deleteRuntimeProcessInstance(dto);
        System.out.println(JSONObject.toJSON(dto));
    }

}
