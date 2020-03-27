package com.polaris.workflow.service;

import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;

import com.polaris.workflow.dto.WorkflowDto;

public interface WorkflowCreateService {
    public static String PROCESS = "process";
    public static String ID = "id";
    public static String NAME = "name";
    public static String CANDIDATE_GROUPS = "candidateGroups";
    public static String COND_EXP = "conditionExpression";
    public static String START_EVENT = "startEvent";
    public static String END_EVENT = "endEvent";
    public static String SEQUENCE_FLOW = "sequenceFlow";
    public static String USER_TASK = "userTask";
    public static String PA_GW = "parallelGateway";
    public static String EX_GW = "exclusiveGateway";
    public static String FROM = "sourceRef";
    public static String TO = "targetRef";
    public static final String prefix = "diagrams/";
    public static final String suffix = ".bpmn";
    
    /*BpmnModel*/
	public BpmnModel createBpmnModel(); 
	public BpmnModel createBpmnModel(String json);
	public BpmnModel createBpmnModel(Map<String, Object> diagramMap);

    /*Process*/
    public Process createProcess(String id, String name); 
    public List<Map<String, Object>> getAllProcess(Map<String, Object> diagramMap);
    
    /*任务节点*/  
    public UserTask createUserTask(String id, String name);
    public UserTask createUserTask(String id, String name, List<String> candidateGroups);
    public UserTask createUserTask(Map<String, Object> userTaskMap);
    public List<Map<String, Object>> getAllTaskMap(Map<String, Object> processMap);
  
    /*连线*/ 
    public SequenceFlow createSequenceFlow(String sourceRef, String targetRef);
    public SequenceFlow createSequenceFlow(String sourceRef, String targetRef,String name);
    public SequenceFlow createSequenceFlow(String sourceRef, String targetRef,String name,String conditionExpression);
    public SequenceFlow createSequenceFlow(Map<String, Object> sequenceFlowMap);
    public List<Map<String, Object>> getAllSequenceFlowMap(Map<String, Object> processMap);
      
    /*排他网关*/  
    public ExclusiveGateway createExclusiveGateway(String id);
    public ExclusiveGateway createExclusiveGateway(String id, String name);
    public ExclusiveGateway createExclusiveGateway(Map<String, Object> exclusiveGatewayMap);
    public List<Map<String, Object>> getAllExclusiveGatewayMap(Map<String, Object> processMap);
    
    /*并行网关*/  
    public ParallelGateway createParallelGateway(String id);
    public ParallelGateway createParallelGateway(String id, String name);
    public ParallelGateway createParallelGateway(Map<String, Object> parallelGatewayMap);
    public List<Map<String, Object>> getAllParallelGatewayMap(Map<String, Object> processMap);
  
    /*开始节点*/  
    public StartEvent createStartEvent();
    public StartEvent createStartEvent(String id, String name);  
    public StartEvent createStartEvent(Map<String, Object> startEventMap); 
    
  
    /*结束节点*/  
    public EndEvent createEndEvent();
    public EndEvent createEndEvent(String id, String name);
    public EndEvent createEndEvent(Map<String, Object> endEventMap);
    
    /**
     * 动态创建流程Map
     *
     * @param map
     */
    public Map<String, Object> createDiagramMap(String json);
    
    /**
     * 动态创建流程
     *
     * @param WorkflowDto dto
     */
    public String createDiagram(String name,Process... processes);
    public String createDiagram(String name,BpmnModel bpmnModel,Process... processes);
    
    /**
     * 部署单个流程定义
     *
     * @param WorkflowDto dto
     */
    public WorkflowDto deployDiagram(String name, String exportDir);

    /**
     * 删除部署的流程，级联删除流程实例
     *
     * @param deploymentId 流程部署ID
     */
    public WorkflowDto deleteDiagram(String deploymentId);
    
    /**
     * 获取流程
     *
     * @param entity
     */
    public WorkflowDto getDiagram(String processDefinitionId);
    
    /**
     * 查询当前系统中已经部署的流程
     *
     * @return 流程的名称和key的集合
     * @throws Exception
     */
	public WorkflowDto getAllDeployment();
}
