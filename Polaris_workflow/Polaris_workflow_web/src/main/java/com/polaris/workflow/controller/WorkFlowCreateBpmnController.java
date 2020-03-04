package com.polaris.workflow.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.polaris.core.util.FileUtil;
import com.polaris.workflow.api.dto.WorkflowDto;
import com.polaris.workflow.api.service.WorkflowService;

@Component
@Path("/create")
public class WorkFlowCreateBpmnController {
	
	@Autowired
	WorkflowService workflowService;
	
	/**
     * 部署单个流程定义
     *
     */
    @POST
    @Path("/bpmn")
    @Produces(MediaType.APPLICATION_JSON)
    public String bpmn(@Context HttpServletRequest request) {
    	
    	try {
        	test01();
    	} catch (Exception ex) {
    		
    	}
    	  
    	return "";
    }
    
    public void test01() throws IOException {  
        System.out.println(".........start...");  
        //ProcessEngine processEngine=getProcessEngine();  
        //workflowService.
          
        // 1. Build up the model from scratch  
        BpmnModel model = new BpmnModel();    
        Process process=new Process();  
        model.addProcess(process);   
        final String PROCESSID ="process02";  
        final String PROCESSNAME ="测试02";  
        process.setId(PROCESSID);   
        process.setName(PROCESSNAME);  
          
        process.addFlowElement(createStartEvent());    
        process.addFlowElement(createUserTask("task1", "节点01", "candidateGroup1"));   
        process.addFlowElement(createExclusiveGateway("createExclusiveGateway1"));   
        process.addFlowElement(createUserTask("task2", "节点02", "candidateGroup2"));   
        process.addFlowElement(createExclusiveGateway("createExclusiveGateway2"));   
        process.addFlowElement(createUserTask("task3", "节点03", "candidateGroup3"));   
        process.addFlowElement(createExclusiveGateway("createExclusiveGateway3"));   
        process.addFlowElement(createUserTask("task4", "节点04", "candidateGroup4"));  
        process.addFlowElement(createEndEvent());    
          
        process.addFlowElement(createSequenceFlow("startEvent", "task1", "", ""));   
        process.addFlowElement(createSequenceFlow("task1", "task2", "", ""));   
        process.addFlowElement(createSequenceFlow("task2", "createExclusiveGateway1", "", ""));  
        process.addFlowElement(createSequenceFlow("createExclusiveGateway1", "task1", "不通过", "${pass=='2'}"));  
        process.addFlowElement(createSequenceFlow("createExclusiveGateway1", "task3", "通过", "${pass=='1'}"));   
        process.addFlowElement(createSequenceFlow("task3", "createExclusiveGateway2", "", ""));  
        process.addFlowElement(createSequenceFlow("createExclusiveGateway2", "task2", "不通过", "${pass=='2'}"));  
        process.addFlowElement(createSequenceFlow("createExclusiveGateway2", "task4", "通过", "${pass=='1'}"));  
        process.addFlowElement(createSequenceFlow("task4", "createExclusiveGateway3", "", ""));  
        process.addFlowElement(createSequenceFlow("createExclusiveGateway3", "task3", "不通过", "${pass=='2'}"));  
        process.addFlowElement(createSequenceFlow("createExclusiveGateway3", "endEvent", "通过", "${pass=='1'}"));  
          
        // 3. Deploy the process to the engine 
        WorkflowDto dto = new WorkflowDto();
        dto.setBpmnModel(model);
        dto.setProcessDefinitionKey(PROCESSID);
        dto = workflowService.createDiagram(dto);
        
        dto.setUserId("adfad");
        dto.setVariable("test", "test");
        dto.setBusinessKey("afdadfadsfad");
        workflowService.startWorkflow(dto);
        
        dto = workflowService.getProcessDiagram(dto);

        //Deployment deployment = processEngine.getRepositoryService().createDeployment().addBpmnModel(PROCESSID+".bpmn", model).name(PROCESSID+"_deployment").deploy();    
             
//        // 4. Start a process instance    
//        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(PROCESSID);   
          
//        // 5. Check if task is available    
//        List<Task> tasks = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).list();  
          
        // 6. Save process diagram to a file      
//        InputStream processDiagram = processEngine.getRepositoryService().getProcessDiagram(processInstance.getProcessDefinitionId());   
        String fileName = FileUtil.getFullPath("deployments/"+dto.getProcessDefinitionKey()+".png");
        FileUtils.copyInputStreamToFile(dto.getProcessDiagram(), new File(fileName));    
             
          
        System.out.println(".........end...");  
    }  
      

    /*任务节点*/  
    protected static UserTask createUserTask(String id, String name, String candidateGroup) {  
        List<String> candidateGroups=new ArrayList<String>();  
        candidateGroups.add(candidateGroup);  
        UserTask userTask = new UserTask();  
        userTask.setName(name);  
        userTask.setId(id);  
        userTask.setCandidateGroups(candidateGroups);  
        return userTask;  
    }  
  
    /*连线*/  
    protected static SequenceFlow createSequenceFlow(String from, String to,String name,String conditionExpression) {  
        SequenceFlow flow = new SequenceFlow();  
        flow.setSourceRef(from);  
        flow.setTargetRef(to);  
        flow.setName(name);  
        if(!StringUtils.isEmpty(conditionExpression)){  
            flow.setConditionExpression(conditionExpression);  
        }         
        return flow;  
    }  
      
    /*排他网关*/  
    protected static ExclusiveGateway createExclusiveGateway(String id) {  
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();  
        exclusiveGateway.setId(id);  
        return exclusiveGateway;  
    }  
  
    /*开始节点*/  
    protected static StartEvent createStartEvent() {  
        StartEvent startEvent = new StartEvent();  
        startEvent.setId("startEvent");  
        return startEvent;  
    }  
  
    /*结束节点*/  
    protected static EndEvent createEndEvent() {  
        EndEvent endEvent = new EndEvent();  
        endEvent.setId("endEvent");  
        return endEvent;  
    }  
}
