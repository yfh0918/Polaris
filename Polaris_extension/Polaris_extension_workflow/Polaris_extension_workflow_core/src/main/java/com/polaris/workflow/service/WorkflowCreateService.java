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

import com.polaris.core.Constant;
import com.polaris.core.util.FileUtil;
import com.polaris.core.util.StringUtil;
import com.polaris.workflow.dto.WorkflowDto;
import com.polaris.workflow.util.WorkflowUtils;

import cn.hutool.core.collection.CollectionUtil;

@Component
public class WorkflowCreateService {
	
    private static Logger logger = LoggerFactory.getLogger(WorkflowCreateService.class);
    
    @Autowired
    private RepositoryService repositoryService;
    
    public static final String prefix = "diagrams/";
    public static final String suffix = ".bpmn";
    
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
        		prefix+processDefinitionKey+suffix, bpmnModel).name(processDefinitionKey).deploy(); 
        
        // save bpmn
        try {
            InputStream processBpmn = repositoryService.getResourceAsStream(deployment.getId(), prefix+processDefinitionKey+suffix);
            String fileName = FileUtil.getFullPath(prefix+processDefinitionKey+suffix);
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
        		prefix+processDefinitionKey+suffix, bpmnModel).name(processDefinitionKey).deploy(); 
        
        // save bpmn
        try {
            InputStream processBpmn = repositoryService.getResourceAsStream(deployment.getId(), prefix+processDefinitionKey+suffix);
            String fileName = FileUtil.getFullPath(prefix+processDefinitionKey+suffix);
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
    @Transactional
    public WorkflowDto deployDiagram(String processDefinitionKey, String exportDir) {
    	WorkflowDto result = new WorkflowDto();
       
        if (StringUtil.isEmpty(processDefinitionKey)) {
        	result.setMessage(WorkflowDto.MESSAGE_INFO[1]);
        	result.setCode(String.valueOf(Constant.RESULT_FAIL));
            return result;
        }
        

        try {
            //载入流程
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            String classpathResourceUrl = prefix + processDefinitionKey + suffix;
            logger.debug("read workflow from: {}", classpathResourceUrl);
            Resource resource = resourceLoader.getResource(classpathResourceUrl);
            InputStream inputStream = null;
            inputStream = resource.getInputStream();
            if (inputStream == null) {
                logger.warn("ignore deploy workflow module: {}", classpathResourceUrl);
            } else {
                logger.debug("finded workflow module: {}, deploy it!", classpathResourceUrl);
                Deployment deployment = repositoryService.createDeployment().name(processDefinitionKey).addClasspathResource(classpathResourceUrl).deploy();
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
    @SuppressWarnings("unchecked")
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
