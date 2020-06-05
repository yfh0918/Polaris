package com.polaris.workflow.service;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.converter.BpmnXMLConverter;
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
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.polaris.core.Constant;
import com.polaris.core.util.BeanUtil;
import com.polaris.core.util.FileUtil;
import com.polaris.core.util.JacksonUtil;
import com.polaris.core.util.StringUtil;
import com.polaris.workflow.dto.WorkflowDto;
import com.polaris.workflow.util.WorkflowUtils;

import cn.hutool.core.collection.CollectionUtil;

@SuppressWarnings("unchecked")
@Component
public class WorkflowCreateServiceImpl implements WorkflowCreateService{
	
    private static Logger logger = LoggerFactory.getLogger(WorkflowCreateService.class);

    @Autowired
    private RepositoryService repositoryService;

    /*Model*/ 
    @Override
    public BpmnModel createBpmnModel() {  
        return new BpmnModel();  
    }  
    @Override
	public BpmnModel createBpmnModel(String json) {
		Map<String, Object> diagramMap = createDiagramMap(json);
		return createBpmnModel(diagramMap);
	}
    @Override
	public BpmnModel createBpmnModel(Map<String, Object> diagramMap) {
		List<Map<String, Object>> processList = (List<Map<String, Object>>)diagramMap.get(PROCESS);
		List<Process> processObjList = new ArrayList<>();
		for (Map<String, Object> process : processList) {
			if (process.get(NAME) == null) {
				process.put(NAME, process.get(ID));
			}
			Process processObj = createProcess(process.get(ID).toString(), process.get(NAME).toString()); 
			processObjList.add(processObj);
			
			//START_EVENT
			Map<String, Object> startEvent = (Map<String, Object>)process.get(START_EVENT);
			processObj.addFlowElement(createStartEvent(startEvent)); 
			
			//USER_TASK
			if (process.get(USER_TASK) != null) {
				List<Map<String, Object>> userTaskList = (List<Map<String, Object>>)process.get(USER_TASK);
				for (Map<String, Object> userTask : userTaskList) {
					processObj.addFlowElement(createUserTask(userTask));
				}
			}
			
			//PA_GW
			if (process.get(PA_GW) != null) {
				List<Map<String, Object>> paGatewayList = (List<Map<String, Object>>)process.get(PA_GW);
				for (Map<String, Object> paGateway : paGatewayList) {
					processObj.addFlowElement(createParallelGateway(paGateway));
				}
			}
			
			//EX_GW
			if (process.get(EX_GW) != null) {
				List<Map<String, Object>> exGatewayList = (List<Map<String, Object>>)process.get(EX_GW);
				for (Map<String, Object> exGateway : exGatewayList) {
					processObj.addFlowElement(createExclusiveGateway(exGateway));
				}
			}
			
			//END_EVENT
			Map<String, Object> endEvent = (Map<String, Object>)process.get(END_EVENT);
			processObj.addFlowElement(createEndEvent(endEvent)); 
			
			//SEQUENCE_FLOW
			if (process.get(SEQUENCE_FLOW) != null) {
				List<Map<String, Object>> sequenceFlowList =(List<Map<String, Object>>)process.get(SEQUENCE_FLOW);
				for (Map<String, Object> sequenceFlow : sequenceFlowList) {
					processObj.addFlowElement(createSequenceFlow(sequenceFlow));
				}
			}
		}
		
		BpmnModel bpmnModel = new BpmnModel();
		for (Process process: processObjList) {
			bpmnModel.addProcess(process);
		}
		return bpmnModel;
    }

    /*Process*/
    @Override
    public Process createProcess(String id, String name) {  
    	Process process=new Process();  
        process.setId(id);   
        process.setName(name);
        return process;
    } 
    @Override
    public List<Map<String, Object>> getAllProcess(Map<String, Object> diagramMap) {
    	return (List<Map<String, Object>>)diagramMap.get(PROCESS);
    }
    
    /*任务节点*/  
    @Override
    public UserTask createUserTask(String id, String name) { 
    	return createUserTask(id,name,null);
    }
    @Override
    public UserTask createUserTask(String id, String name, List<String> candidateGroups) {  
        UserTask userTask = new UserTask();  
        userTask.setName(name);  
        userTask.setId(id);  
        if (candidateGroups != null) {
        	userTask.setCandidateGroups(candidateGroups);  
        }
        return userTask;  
    } 
    @Override
    public UserTask createUserTask(Map<String, Object> userTaskMap) { 
    	return BeanUtil.toBean(UserTask.class, JacksonUtil.toJson(userTaskMap), true);
    }
    @Override
    public List<Map<String, Object>> getAllTaskMap(Map<String, Object> processMap) {
    	return (List<Map<String, Object>>)processMap.get(USER_TASK);
    }
  
    /*连线*/ 
    @Override
    public SequenceFlow createSequenceFlow(String sourceRef, String targetRef) { 
    	return createSequenceFlow(sourceRef,targetRef,null,null);
    }
    @Override
    public SequenceFlow createSequenceFlow(String sourceRef, String targetRef,String name) { 
    	return createSequenceFlow(sourceRef,targetRef,name,null);
    }
    @Override
    public SequenceFlow createSequenceFlow(String sourceRef, String targetRef,String name,String conditionExpression) {  
        SequenceFlow flow = new SequenceFlow();  
        flow.setSourceRef(sourceRef);  
        flow.setTargetRef(targetRef);  
        if (!StringUtils.isEmpty(name)) {
        	flow.setName(name);  
        }
        if(!StringUtils.isEmpty(conditionExpression)){  
            flow.setConditionExpression(conditionExpression);  
        }         
        return flow;  
    }
    @Override
    public SequenceFlow createSequenceFlow(Map<String, Object> sequenceFlowMap) { 
    	return BeanUtil.toBean(SequenceFlow.class, JacksonUtil.toJson(sequenceFlowMap), true);
    }
    @Override
    public List<Map<String, Object>> getAllSequenceFlowMap(Map<String, Object> processMap) {
    	return (List<Map<String, Object>>)processMap.get(SEQUENCE_FLOW);
    }

      
    /*排他网关*/  
    @Override
    public ExclusiveGateway createExclusiveGateway(String id) {  
    	return createExclusiveGateway(id,null);
    }
    @Override
    public ExclusiveGateway createExclusiveGateway(String id, String name) {  
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();  
        exclusiveGateway.setId(id);
        if (!StringUtils.isEmpty(name)) {
        	exclusiveGateway.setName(name);
    	}
        return exclusiveGateway;  
    }
    @Override
    public ExclusiveGateway createExclusiveGateway(Map<String, Object> exclusiveGatewayMap) { 
    	return BeanUtil.toBean(ExclusiveGateway.class, JacksonUtil.toJson(exclusiveGatewayMap), true);
    }
    @Override
    public List<Map<String, Object>> getAllExclusiveGatewayMap(Map<String, Object> processMap) {
    	return (List<Map<String, Object>>)processMap.get(EX_GW);
    }
    
    /*并行网关*/ 
    @Override
    public ParallelGateway createParallelGateway(String id) {
    	return createParallelGateway(id,null);
    }
    @Override
    public ParallelGateway createParallelGateway(String id, String name) {  
    	ParallelGateway parallelGateway = new ParallelGateway();  
    	parallelGateway.setId(id);
    	if (!StringUtils.isEmpty(name)) {
    		parallelGateway.setName(name);
    	}
    	
        return parallelGateway;  
    }
    @Override
    public ParallelGateway createParallelGateway(Map<String, Object> parallelGatewayMap) {  
    	return BeanUtil.toBean(ParallelGateway.class, JacksonUtil.toJson(parallelGatewayMap), true);
    }
    @Override
    public List<Map<String, Object>> getAllParallelGatewayMap(Map<String, Object> processMap) {
    	return (List<Map<String, Object>>)processMap.get(PA_GW);
    }
  
    /*开始节点*/  
    @Override
    public StartEvent createStartEvent() { 
    	return createStartEvent(null,null);
    }
    @Override
    public StartEvent createStartEvent(String id, String name) {  
        StartEvent startEvent = new StartEvent();  
        if (StringUtils.isEmpty(id)) {
        	id = "startEvent";
        }
        startEvent.setId(id); 
        if (!StringUtils.isEmpty(name)) {
        	startEvent.setName(name);
        }
        return startEvent;  
    }  
    @Override
    public StartEvent createStartEvent(Map<String, Object> startEventMap) {  
         return BeanUtil.toBean(StartEvent.class, JacksonUtil.toJson(startEventMap), true);
    }  
    
  
    /*结束节点*/  
    @Override
    public EndEvent createEndEvent() {
    	return createEndEvent(null,null);
    }
    @Override
    public EndEvent createEndEvent(String id, String name) {  
        EndEvent endEvent = new EndEvent(); 
        if (StringUtils.isEmpty(id)) {
        	id = "endEvent";
        }
        endEvent.setId(id);  
        if (!StringUtils.isEmpty(name)) {
        	endEvent.setName(name);
        }
        return endEvent;  
    }
    @Override
    public EndEvent createEndEvent(Map<String, Object> endEventMap) {  
        return BeanUtil.toBean(EndEvent.class, JacksonUtil.toJson(endEventMap), true);
    }
    
    /**
     * 动态创建流程Map
     *
     * @param map
     */
    @Override
    public Map<String, Object> createDiagramMap(String json) {
        return JacksonUtil.toObj(json, new TypeReference<Map<String, Object>>(){});
    }
    /**
     * 动态创建流程
     *
     * @param WorkflowDto dto
     */
    @Override
    @Transactional
    public String createDiagram(String name,Process... processes) {
    	BpmnModel bpmnModel = createBpmnModel(); 
    	return createDiagram(name,bpmnModel,processes);

    }
    @Override
    @Transactional
    public String createDiagram(String name,BpmnModel bpmnModel,Process... processes) {
    	if (processes != null) {
    		for (Process process : processes) {
        		bpmnModel.addProcess(process);
        	}
    	}
    	
        // 2. Generate graphical information    
        new BpmnAutoLayout(bpmnModel).execute();  
          
        // 3. Deploy the process to the engine    
        Deployment deployment = repositoryService.createDeployment().addBpmnModel(
        		prefix+name+suffix, bpmnModel).name(name).deploy(); 
        
        // save bpmn
        try {
            InputStream processBpmn = repositoryService.getResourceAsStream(deployment.getId(), prefix+name+suffix);
            String fileName = FileUtil.getFullPath(prefix+name+suffix);
            FileUtils.copyInputStreamToFile(processBpmn,new File(fileName));  
        } catch (Exception ex) {
        	logger.error("create BPMN file error：{}",ex);
        }

        return deployment.getId();

    }
    
    /**
     * 部署单个流程定义
     *
     * @param WorkflowDto dto
     */
    @Override
    @Transactional
    public WorkflowDto deployDiagram(String name, String exportDir) {
    	WorkflowDto result = new WorkflowDto();
       
        if (StringUtil.isEmpty(name)) {
        	result.setMessage(WorkflowDto.MESSAGE_INFO[1]);
        	result.setCode(String.valueOf(Constant.RESULT_FAIL));
            return result;
        }
        

        try {
            //载入流程
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            String classpathResourceUrl = prefix + name + suffix;
            logger.debug("read workflow from: {}", classpathResourceUrl);
            Resource resource = resourceLoader.getResource(classpathResourceUrl);
            InputStream inputStream = null;
            inputStream = resource.getInputStream();
            if (inputStream == null) {
                logger.warn("ignore deploy workflow module: {}", classpathResourceUrl);
            } else {
                logger.debug("finded workflow module: {}, deploy it!", classpathResourceUrl);
                Deployment deployment = repositoryService.createDeployment().name(name).addClasspathResource(classpathResourceUrl).deploy();
                // export diagram
                List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
                result.setDeploymentId(deployment.getId());
                if (!StringUtil.isEmpty(exportDir)) {
                    for (ProcessDefinition processDefinition : list) {
                        WorkflowUtils.exportDiagramToFile(repositoryService, processDefinition, exportDir);
                    }
                }
            }
            result.setCode(Constant.RESULT_SUCCESS);
        } catch (Exception ex) {
            logger.error("异常", ex);
            result.setCode(String.valueOf(Constant.RESULT_FAIL));
            result.setMessage(ex.getMessage());
        }
        return result;
    }

    /**
     * 删除部署的流程，级联删除流程实例
     *
     * @param deploymentId 流程部署ID
     */
    @Override
    @Transactional
    public WorkflowDto deleteDiagram(String deploymentId) {

        //参数检查
    	WorkflowDto dto = new WorkflowDto();
        if (StringUtil.isEmpty(deploymentId)) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[5]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }

        //删除发布图
        try {
            repositoryService.deleteDeployment(deploymentId, true);
            dto.setCode(Constant.RESULT_SUCCESS);
        } catch (Exception ex) {
            logger.error("异常", ex);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            dto.setMessage(ex.getMessage());
        }
        return dto;
    }
    
    /**
     * 获取流程
     *
     * @param entity
     */
    @Override
    public WorkflowDto getDiagram(String processDefinitionId) {

        //参数检查
    	WorkflowDto dto = new WorkflowDto();
        if (StringUtil.isEmpty(processDefinitionId)) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[13]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }
        InputStream processDiagram = repositoryService.getProcessDiagram(processDefinitionId); 
        dto.setProcessDiagram(processDiagram);
        dto.setCode(Constant.RESULT_SUCCESS);
        return dto;
    }
    
    /**
     * 查询当前系统中已经部署的流程
     *
     * @return 流程的名称和key的集合
     * @throws Exception
     */
    @Override
	public WorkflowDto getAllDeployment() {
    	
        List<Deployment> deploymentList = repositoryService.createDeploymentQuery().list();
        //查询ACT_RE_PROCDEF，获取中文名称  只有开启流程才会有中文名称
        List<Map<String, Object>> resultList = new ArrayList<>();
        String key;
        InputStream resouceStream;
        InputStreamReader in;
        XMLStreamReader xtr;
        BpmnModel model;
        XMLInputFactory xif = XMLInputFactory.newInstance();
        List<Process> processes;

        for (Deployment deployment : deploymentList) {
            //获取部署的流程的key名称
            key = deployment.getName();
            //由于没有启动的流程回去不到中文名称,只能从生成xml文件读取
            try {
                resouceStream = repositoryService.getResourceAsStream(deployment.getId(), prefix+ key + suffix);
                in = new InputStreamReader(resouceStream, Constant.UTF_CODE);
                xtr = xif.createXMLStreamReader(in);
            } catch (Exception e) {
                logger.error("解析工作流文件出错，ERROR:", e);
                continue;
            }
            model = new BpmnXMLConverter().convertToBpmnModel(xtr);
            //创建map，存放流程的key及中文名称
            processes = model.getProcesses();
            if (CollectionUtil.isEmpty(processes)) {
                continue;
            }
            Map<String, Object> tmpMap = new HashMap<>();
            tmpMap.put("showValue", processes.get(0).getDocumentation());
            tmpMap.put("definitionKey", processes.get(0).getId());
            resultList.add(tmpMap);
        }
        WorkflowDto dto = new WorkflowDto();
        dto.setData(resultList);
        return dto;
    }
}
