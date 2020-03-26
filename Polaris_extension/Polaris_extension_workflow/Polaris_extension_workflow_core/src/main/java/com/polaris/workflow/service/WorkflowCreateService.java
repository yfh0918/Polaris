package com.polaris.workflow.service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.polaris.core.util.FileUtil;

@Component
public class WorkflowCreateService {
	
    private static Logger logger = LoggerFactory.getLogger(WorkflowCreateService.class);
    
    @Autowired
    private RepositoryService repositoryService;
    
    /*Model*/  
    public BpmnModel createBpmnModel() {  
        return new BpmnModel();  
    }  

    /*Process*/
    public Process createProcess(String id, String name) {  
    	Process process=new Process();  
        process.setId(id);   
        process.setName(name);
        return process;
    } 
    
    /*任务节点*/  
    public UserTask createUserTask(String id, String name, String candidateGroup) {  
        List<String> candidateGroups=new ArrayList<String>();  
        candidateGroups.add(candidateGroup);  
        UserTask userTask = new UserTask();  
        userTask.setName(name);  
        userTask.setId(id);  
        userTask.setCandidateGroups(candidateGroups);  
        return userTask;  
    }  
  
    /*连线*/  
    public SequenceFlow createSequenceFlow(String from, String to,String name,String conditionExpression) {  
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
    public ExclusiveGateway createExclusiveGateway(String id) {  
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();  
        exclusiveGateway.setId(id);  
        return exclusiveGateway;  
    }
    
    /*并行网关*/  
    public ParallelGateway createParallelGateway(String id) {  
    	ParallelGateway parallelGateway = new ParallelGateway();  
    	parallelGateway.setId(id);  
        return parallelGateway;  
    }
  
    /*开始节点*/  
    public StartEvent createStartEvent() {  
        StartEvent startEvent = new StartEvent();  
        startEvent.setId("startEvent");  
        return startEvent;  
    }  
  
    /*结束节点*/  
    public EndEvent createEndEvent() {  
        EndEvent endEvent = new EndEvent();  
        endEvent.setId("endEvent");  
        return endEvent;  
    }
    
    /**
     * 动态创建流程
     *
     * @param WorkflowDto dto
     */
    @Transactional
    public String createDiagram(String processDefinitionKey,Process... processes) {
    	BpmnModel bpmnModel = createBpmnModel(); 
    	if (processes == null) {
    		return null;
    	}
    	for (Process process : processes) {
    		bpmnModel.addProcess(process);
    	}
    	
        // 2. Generate graphical information    
        new BpmnAutoLayout(bpmnModel).execute();  
          
        // 3. Deploy the process to the engine    
        Deployment deployment = repositoryService.createDeployment().addBpmnModel(
        		processDefinitionKey+".bpmn", bpmnModel).name(processDefinitionKey+"_deployment").deploy(); 
        
        // save bpmn
        try {
            InputStream processBpmn = repositoryService.getResourceAsStream(deployment.getId(), processDefinitionKey+".bpmn");
            String fileName = FileUtil.getFullPath("deployments/"+processDefinitionKey+".bpmn");
            FileUtils.copyInputStreamToFile(processBpmn,new File(fileName));  
        } catch (Exception ex) {
        	logger.error("create BPMN file error：{}",ex);
        }

        return deployment.getId();

    }
    
    @Transactional
    public String createDiagram(String processDefinitionKey,BpmnModel bpmnModel) {
    	
        // 2. Generate graphical information    
        new BpmnAutoLayout(bpmnModel).execute();  
          
        // 3. Deploy the process to the engine    
        Deployment deployment = repositoryService.createDeployment().addBpmnModel(
        		processDefinitionKey+".bpmn", bpmnModel).name(processDefinitionKey+"_deployment").deploy(); 
        
        // save bpmn
        try {
            InputStream processBpmn = repositoryService.getResourceAsStream(deployment.getId(), processDefinitionKey+".bpmn");
            String fileName = FileUtil.getFullPath("deployments/"+processDefinitionKey+".bpmn");
            FileUtils.copyInputStreamToFile(processBpmn,new File(fileName));  
        } catch (Exception ex) {
        	logger.error("create BPMN file error：{}",ex);
        }

        return deployment.getId();

    }
}
