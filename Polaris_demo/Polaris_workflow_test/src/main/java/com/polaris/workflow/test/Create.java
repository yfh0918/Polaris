package com.polaris.workflow.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.Process;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
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
    
//    {
//        "process": [
//            {
//                "sequenceFlow": [
//                    {
//                        "conditionExpression": "",
//                        "from": "startEvent",
//                        "to": "task1"
//                    },
//                    {
//                        "conditionExpression": "",
//                        "from": "task1",
//                        "to": "parallelGateway1"
//                    },
//                    {
//                        "conditionExpression": "",
//                        "from": "parallelGateway1",
//                        "to": "task2"
//                    },
//                    {
//                        "conditionExpression": "",
//                        "from": "parallelGateway1",
//                        "to": "task3"
//                    },
//                    {
//                        "conditionExpression": "",
//                        "from": "task2",
//                        "to": "parallelGateway2"
//                    },
//                    {
//                        "conditionExpression": "",
//                        "from": "task3",
//                        "to": "parallelGateway2"
//                    },
//                    {
//                        "conditionExpression": "",
//                        "from": "parallelGateway2",
//                        "to": "task4"
//                    },
//                    {
//                        "conditionExpression": "",
//                        "from": "task4",
//                        "to": "endEvent"
//                    }
//                ],
//                "endEvent": {
//                    "name": "",
//                    "id": "endEvent"
//                },
//                "parallelGateway": [
//                    {
//                        "name": "parallelGateway1",
//                        "id": "parallelGateway1"
//                    },
//                    {
//                        "name": "parallelGateway2",
//                        "id": "parallelGateway2"
//                    }
//                ],
//                "startEvent": {
//                    "name": "",
//                    "id": "startEvent"
//                },
//                "name": "testParallel5",
//                "id": "testParallel5",
//                "userTask": [
//                    {
//                        "candidateGroup": "",
//                        "name": "节点1",
//                        "id": "task1"
//                    },
//                    {
//                        "candidateGroup": "",
//                        "name": "节点2",
//                        "id": "task2"
//                    },
//                    {
//                        "candidateGroup": "",
//                        "name": "节点3",
//                        "id": "task3"
//                    },
//                    {
//                        "candidateGroup": "",
//                        "name": "节点4",
//                        "id": "task4"
//                    }
//                ]
//            }
//        ],
//        "name": "testParallel5"
//    }
	public String create(String processDefinitionKey) {  
//		Process process = null;
//		if (StringUtil.isEmpty(processJson)) {
//	        process=workflowCreateService.createProcess(processDefinitionKey, processDefinitionKey);          
//	        process.addFlowElement(workflowCreateService.createStartEvent());    
//	        process.addFlowElement(workflowCreateService.createUserTask("task1", "节点01"));   
//	        process.addFlowElement(workflowCreateService.createParallelGateway("createParallelGateway1"));   
//	        process.addFlowElement(workflowCreateService.createUserTask("task2", "节点02"));   
//	        process.addFlowElement(workflowCreateService.createUserTask("task3", "节点03"));   
//	        process.addFlowElement(workflowCreateService.createParallelGateway("createParallelGateway2"));   
//	        process.addFlowElement(workflowCreateService.createUserTask("task4", "节点04"));  
//	        process.addFlowElement(workflowCreateService.createEndEvent());    
//	          
//	        process.addFlowElement(workflowCreateService.createSequenceFlow("startEvent", "task1"));   
//	        process.addFlowElement(workflowCreateService.createSequenceFlow("task1", "createParallelGateway1"));  
//	        process.addFlowElement(workflowCreateService.createSequenceFlow("createParallelGateway1", "task2"));  
//	        process.addFlowElement(workflowCreateService.createSequenceFlow("createParallelGateway1", "task3"));  
//	        process.addFlowElement(workflowCreateService.createSequenceFlow("task2", "createParallelGateway2"));  
//	        process.addFlowElement(workflowCreateService.createSequenceFlow("task3", "createParallelGateway2"));  
//	        process.addFlowElement(workflowCreateService.createSequenceFlow("createParallelGateway2", "task4"));  
//	        process.addFlowElement(workflowCreateService.createSequenceFlow("task4", "endEvent"));  
//		} else {
//			//process = JSONObject.parseObject(processJson, Process.class);
//		}
		
		//Map<String, List<Map<String, List<Map<String, String>>>>> processMap = new HashMap<>();
		Map<String, Object> deployment = new HashMap<>();
		deployment.put("name", processDefinitionKey);
		
		List<Map<String, Object>> processList = new ArrayList<>();
		deployment.put("process", processList);
		
		//processMap
		Map<String, Object> processMap = new HashMap<>();
		processList.add(processMap);

		//processMap
		processMap.put("id", processDefinitionKey);
		processMap.put("name", processDefinitionKey);
		
		//startEvent
		Map<String, String> startEvent = new HashMap<>();
		processMap.put("startEvent", startEvent);
		startEvent.put("id", "startEvent");
		startEvent.put("name", "");
		
		//task
		List<Map<String, String>> userTaskList = new ArrayList<>();
		processMap.put("userTask", userTaskList);
		for (int i0 = 1; i0 < 5; i0++) {
			Map<String, String> userTask = new HashMap<>();
			userTask.put("id", "task"+i0);
			userTask.put("name", "节点"+i0);
			userTask.put("candidateGroup", "");
			userTaskList.add(userTask);
		}
		
		//parallelGateway
		List<Map<String, String>> parallelGatewayList = new ArrayList<>();
		processMap.put("parallelGateway", parallelGatewayList);
		for (int i0 = 1; i0 < 3; i0++) {
			Map<String, String> parallelGateway = new HashMap<>();
			parallelGateway.put("id", "parallelGateway"+i0);
			parallelGateway.put("name", "parallelGateway"+i0);
			parallelGatewayList.add(parallelGateway);
		}
		
		//endEvent
		Map<String, String> endEvent = new HashMap<>();
		processMap.put("endEvent", endEvent);
		endEvent.put("id", "endEvent");
		endEvent.put("name", "");
		
		//sequenceFlow
		//String from, String to,String name,String conditionExpression
		List<Map<String, String>> sequenceFlowList = new ArrayList<>();
		processMap.put("sequenceFlow", sequenceFlowList);
		Map<String, String> sequenceFlow0 = new HashMap<>();
		sequenceFlow0.put("from", "startEvent");
		sequenceFlow0.put("to", "task1");
		sequenceFlow0.put("conditionExpression", "");
		sequenceFlowList.add(sequenceFlow0);
		
		Map<String, String> sequenceFlow1 = new HashMap<>();
		sequenceFlow1.put("from", "task1");
		sequenceFlow1.put("to", "parallelGateway1");
		sequenceFlow1.put("conditionExpression", "");
		sequenceFlowList.add(sequenceFlow1);

		Map<String, String> sequenceFlow2 = new HashMap<>();
		sequenceFlow2.put("from", "parallelGateway1");
		sequenceFlow2.put("to", "task2");
		sequenceFlow2.put("conditionExpression", "");
		sequenceFlowList.add(sequenceFlow2);

		Map<String, String> sequenceFlow3 = new HashMap<>();
		sequenceFlow3.put("from", "parallelGateway1");
		sequenceFlow3.put("to", "task3");
		sequenceFlow3.put("conditionExpression", "");
		sequenceFlowList.add(sequenceFlow3);

		Map<String, String> sequenceFlow4 = new HashMap<>();
		sequenceFlow4.put("from", "task2");
		sequenceFlow4.put("to", "parallelGateway2");
		sequenceFlow4.put("conditionExpression", "");
		sequenceFlowList.add(sequenceFlow4);
		
		Map<String, String> sequenceFlow5 = new HashMap<>();
		sequenceFlow5.put("from", "task3");
		sequenceFlow5.put("to", "parallelGateway2");
		sequenceFlow5.put("conditionExpression", "");
		sequenceFlowList.add(sequenceFlow5);
		
		Map<String, String> sequenceFlow6 = new HashMap<>();
		sequenceFlow6.put("from", "parallelGateway2");
		sequenceFlow6.put("to", "task4");
		sequenceFlow6.put("conditionExpression", "");
		sequenceFlowList.add(sequenceFlow6);

		Map<String, String> sequenceFlow7 = new HashMap<>();
		sequenceFlow7.put("from", "task4");
		sequenceFlow7.put("to", "endEvent");
		sequenceFlow7.put("conditionExpression", "");
		sequenceFlowList.add(sequenceFlow7);
    	
		String json = JSON.toJSONString(deployment);
    	System.out.println(json);
    	return workflowCreateService.createDiagram(json);
    } 
      

}
