package com.polaris.workflow.test;

import java.io.File;
import java.io.IOException;

import org.activiti.bpmn.model.Process;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.polaris.core.util.FileUtil;
import com.polaris.workflow.dto.WorkflowDto;
import com.polaris.workflow.service.WorkflowCreateService;
import com.polaris.workflow.service.WorkflowProcessService;

@Service
public class Create {
	
	@Autowired
	WorkflowCreateService workflowCreateService;
	
	@Autowired
	WorkflowProcessService workflowProcessService;
	
    public void test01() throws IOException {  
        System.out.println(".........start...");  
          
        // 1. Build up the model from scratch  
        final String PROCESSID ="process02";  
        final String PROCESSNAME ="测试02";  

        Process process=workflowCreateService.createProcess(PROCESSID, PROCESSNAME);          
        process.addFlowElement(workflowCreateService.createStartEvent());    
        process.addFlowElement(workflowCreateService.createUserTask("task1", "节点01", "candidateGroup1"));   
        process.addFlowElement(workflowCreateService.createExclusiveGateway("createExclusiveGateway1"));   
        process.addFlowElement(workflowCreateService.createUserTask("task2", "节点02", "candidateGroup2"));   
        process.addFlowElement(workflowCreateService.createExclusiveGateway("createExclusiveGateway2"));   
        process.addFlowElement(workflowCreateService.createUserTask("task3", "节点03", "candidateGroup3"));   
        process.addFlowElement(workflowCreateService.createExclusiveGateway("createExclusiveGateway3"));   
        process.addFlowElement(workflowCreateService.createUserTask("task4", "节点04", "candidateGroup4"));  
        process.addFlowElement(workflowCreateService.createEndEvent());    
          
        process.addFlowElement(workflowCreateService.createSequenceFlow("startEvent", "task1", "", ""));   
        process.addFlowElement(workflowCreateService.createSequenceFlow("task1", "task2", "", ""));   
        process.addFlowElement(workflowCreateService.createSequenceFlow("task2", "createExclusiveGateway1", "", ""));  
        process.addFlowElement(workflowCreateService.createSequenceFlow("createExclusiveGateway1", "task1", "不通过", "${pass=='2'}"));  
        process.addFlowElement(workflowCreateService.createSequenceFlow("createExclusiveGateway1", "task3", "通过", "${pass=='1'}"));   
        process.addFlowElement(workflowCreateService.createSequenceFlow("task3", "createExclusiveGateway2", "", ""));  
        process.addFlowElement(workflowCreateService.createSequenceFlow("createExclusiveGateway2", "task2", "不通过", "${pass=='2'}"));  
        process.addFlowElement(workflowCreateService.createSequenceFlow("createExclusiveGateway2", "task4", "通过", "${pass=='1'}"));  
        process.addFlowElement(workflowCreateService.createSequenceFlow("task4", "createExclusiveGateway3", "", ""));  
        process.addFlowElement(workflowCreateService.createSequenceFlow("createExclusiveGateway3", "task3", "不通过", "${pass=='2'}"));  
        process.addFlowElement(workflowCreateService.createSequenceFlow("createExclusiveGateway3", "endEvent", "通过", "${pass=='1'}"));  
        
        // 3. Deploy the process to the engine 
        String deploymentId = workflowCreateService.createDiagram(PROCESSID,process);
        System.out.println("deploymentId == "+deploymentId);
        
        WorkflowDto dto = new WorkflowDto();
        dto.setProcessDefinitionKey(PROCESSID);
        dto.setUserId("adfad");
        dto.setVariable("test", "test");
        dto.setBusinessKey("afdadfadsfad");
        workflowProcessService.startWorkflow(dto);
        
        dto = workflowCreateService.getDiagram(dto.getProcessInstanceId());

        String fileName = FileUtil.getFullPath("deployments/"+dto.getProcessDefinitionKey()+".png");
        FileUtils.copyInputStreamToFile(dto.getProcessDiagram(), new File(fileName));    
             
          
        System.out.println(".........end...");  
    }  
      

}
