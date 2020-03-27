package com.polaris.workflow.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.polaris.workflow.service.WorkflowCreateService;
import com.polaris.workflow.service.WorkflowProcessService;

@Service
public class Create {
	
	@Autowired
	WorkflowCreateService workflowCreateService;
	
	@Autowired
	WorkflowProcessService workflowProcessService;
	
//	{
//    "process":[
//        {
//            "sequenceFlow":[
//                {
//                    "targetRef":"task1",
//                    "conditionExpression":"",
//                    "sourceRef":"startEvent"
//                },
//                {
//                    "targetRef":"parallelGateway1",
//                    "conditionExpression":"",
//                    "sourceRef":"task1"
//                },
//                {
//                    "targetRef":"task2",
//                    "conditionExpression":"",
//                    "sourceRef":"parallelGateway1"
//                },
//                {
//                    "targetRef":"task3",
//                    "conditionExpression":"",
//                    "sourceRef":"parallelGateway1"
//                },
//                {
//                    "targetRef":"parallelGateway2",
//                    "conditionExpression":"",
//                    "sourceRef":"task2"
//                },
//                {
//                    "targetRef":"parallelGateway2",
//                    "conditionExpression":"",
//                    "sourceRef":"task3"
//                },
//                {
//                    "targetRef":"task4",
//                    "conditionExpression":"",
//                    "sourceRef":"parallelGateway2"
//                },
//                {
//                    "targetRef":"endEvent",
//                    "conditionExpression":"",
//                    "sourceRef":"task4"
//                }
//            ],
//            "endEvent":{
//                "id":"endEvent"
//            },
//            "parallelGateway":[
//                {
//                    "name":"parallelGateway1",
//                    "id":"parallelGateway1"
//                },
//                {
//                    "name":"parallelGateway2",
//                    "id":"parallelGateway2"
//                }
//            ],
//            "startEvent":{
//                "id":"startEvent"
//            },
//            "name":"testParallel8",
//            "id":"testParallel8",
//            "userTask":[
//                {
//                    "candidateGroups":[
//                        "001",
//                        "002"
//                    ],
//                    "name":"节点1",
//                    "id":"task1"
//                },
//                {
//                    "candidateGroups":[
//                        "001",
//                        "002"
//                    ],
//                    "name":"节点2",
//                    "id":"task2"
//                },
//                {
//                    "candidateGroups":[
//                        "001",
//                        "002"
//                    ],
//                    "name":"节点3",
//                    "id":"task3"
//                },
//                {
//                    "candidateGroups":[
//                        "001",
//                        "002"
//                    ],
//                    "name":"节点4",
//                    "id":"task4"
//                }
//            ]
//        }
//    ],
//    "name":"testParallel8"
//}
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
		
		Map<String, Object> deployment = new HashMap<>();
		deployment.put(WorkflowCreateService.NAME, processDefinitionKey);
		
		List<Map<String, Object>> processList = new ArrayList<>();
		deployment.put(WorkflowCreateService.PROCESS, processList);
		
		//processMap
		Map<String, Object> processMap = new HashMap<>();
		processList.add(processMap);

		//processMap
		processMap.put(WorkflowCreateService.ID, processDefinitionKey);
		processMap.put(WorkflowCreateService.NAME, processDefinitionKey);
		
		//startEvent
		Map<String, Object> startEvent = new HashMap<>();
		processMap.put(WorkflowCreateService.START_EVENT, startEvent);
		startEvent.put(WorkflowCreateService.ID, "startEvent");
		
		//task
		List<Map<String, Object>> userTaskList = new ArrayList<>();
		processMap.put(WorkflowCreateService.USER_TASK, userTaskList);
		for (int i0 = 1; i0 < 5; i0++) {
			Map<String, Object> userTask = new HashMap<>();
			userTask.put(WorkflowCreateService.ID, "task"+i0);
			userTask.put(WorkflowCreateService.NAME, "节点"+i0);
			List<String> candidateList = new ArrayList<>();
			candidateList.add("001");
			candidateList.add("002");
			userTask.put(WorkflowCreateService.CANDIDATE_GROUPS, candidateList);
			userTaskList.add(userTask);
		}
		
		//parallelGateway
		List<Map<String, Object>> parallelGatewayList = new ArrayList<>();
		processMap.put(WorkflowCreateService.PA_GW, parallelGatewayList);
		for (int i0 = 1; i0 < 3; i0++) {
			Map<String, Object> parallelGateway = new HashMap<>();
			parallelGateway.put(WorkflowCreateService.ID, "parallelGateway"+i0);
			parallelGateway.put(WorkflowCreateService.NAME, "parallelGateway"+i0);
			parallelGatewayList.add(parallelGateway);
		}
		
		//endEvent
		Map<String, Object> endEvent = new HashMap<>();
		processMap.put(WorkflowCreateService.END_EVENT, endEvent);
		endEvent.put(WorkflowCreateService.ID, "endEvent");
		
		//sequenceFlow
		List<Map<String, Object>> sequenceFlowList = new ArrayList<>();
		processMap.put(WorkflowCreateService.SEQUENCE_FLOW, sequenceFlowList);
		Map<String, Object> sequenceFlow0 = new HashMap<>();
		sequenceFlow0.put(WorkflowCreateService.FROM, "startEvent");
		sequenceFlow0.put(WorkflowCreateService.TO, "task1");
		sequenceFlow0.put(WorkflowCreateService.COND_EXP, "");
		sequenceFlowList.add(sequenceFlow0);
		
		Map<String, Object> sequenceFlow1 = new HashMap<>();
		sequenceFlow1.put(WorkflowCreateService.FROM, "task1");
		sequenceFlow1.put(WorkflowCreateService.TO, "parallelGateway1");
		sequenceFlow1.put(WorkflowCreateService.COND_EXP, "");
		sequenceFlowList.add(sequenceFlow1);

		Map<String, Object> sequenceFlow2 = new HashMap<>();
		sequenceFlow2.put(WorkflowCreateService.FROM, "parallelGateway1");
		sequenceFlow2.put(WorkflowCreateService.TO, "task2");
		sequenceFlow2.put(WorkflowCreateService.COND_EXP, "");
		sequenceFlowList.add(sequenceFlow2);

		Map<String, Object> sequenceFlow3 = new HashMap<>();
		sequenceFlow3.put(WorkflowCreateService.FROM, "parallelGateway1");
		sequenceFlow3.put(WorkflowCreateService.TO, "task3");
		sequenceFlow3.put(WorkflowCreateService.COND_EXP, "");
		sequenceFlowList.add(sequenceFlow3);

		Map<String, Object> sequenceFlow4 = new HashMap<>();
		sequenceFlow4.put(WorkflowCreateService.FROM, "task2");
		sequenceFlow4.put(WorkflowCreateService.TO, "parallelGateway2");
		sequenceFlow4.put(WorkflowCreateService.COND_EXP, "");
		sequenceFlowList.add(sequenceFlow4);
		
		Map<String, Object> sequenceFlow5 = new HashMap<>();
		sequenceFlow5.put(WorkflowCreateService.FROM, "task3");
		sequenceFlow5.put(WorkflowCreateService.TO, "parallelGateway2");
		sequenceFlow5.put(WorkflowCreateService.COND_EXP, "");
		sequenceFlowList.add(sequenceFlow5);
		
		Map<String, Object> sequenceFlow6 = new HashMap<>();
		sequenceFlow6.put(WorkflowCreateService.FROM, "parallelGateway2");
		sequenceFlow6.put(WorkflowCreateService.TO, "task4");
		sequenceFlow6.put(WorkflowCreateService.COND_EXP, "");
		sequenceFlowList.add(sequenceFlow6);

		Map<String, Object> sequenceFlow7 = new HashMap<>();
		sequenceFlow7.put(WorkflowCreateService.FROM, "task4");
		sequenceFlow7.put(WorkflowCreateService.TO, "endEvent");
		sequenceFlow7.put(WorkflowCreateService.COND_EXP, "");
		sequenceFlowList.add(sequenceFlow7);
    	
		String json = JSON.toJSONString(deployment);
    	System.out.println(json);
    	Map<String, Object> diagramMap = workflowCreateService.createDiagramMap(json);
    	return workflowCreateService.createDiagram(
    			diagramMap.get(WorkflowCreateService.NAME).toString(), 
    			workflowCreateService.createBpmnModel(diagramMap));
    } 
      

}
