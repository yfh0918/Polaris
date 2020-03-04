package com.polaris.workflow.api.dto;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.apache.commons.collections.CollectionUtils;

import com.polaris.core.dto.PageDto;
import com.polaris.core.util.StringUtil;

public class WorkflowDto extends PageDto implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	
	//定义错误信息
    public static final String[] MESSAGE_INFO  = { 
    										"没有userId",
    										"没有processDefinitionKey",
    										"没有businessKey", 
    										"没有variables", 
    										"没有taskId",
    										"没有deploymentId",
    										"没有processInstanceId",
    										"businessKey正在使用中",
    										"businessKey已经使用完成",
    										"使用中的businessKey数量超过",
    										"已经完成的businessKey数量超过",
			                                 "网关条件不合法",
			                                 "没有BpmnModel对象",
			                                 "没有processDefinitionId"
    										};
    
    public static final int DEFAULT_MAX_SIZE_BUSINESS_KEY = 1000;//返回的最大businessKey数量
	
    //其他信息
    public static final String OK = "OK";
    public static final String TYPE = "type";
    public static final String COMMENT = "comment";
    
    //构造函数
    public WorkflowDto() {
		variables = new HashMap<String, Object>();
		//参数初期化
		variables.put(COMMENT, null);
		variables.put(ASSIGNEE, null);
		variables.put(CANDIDATE_USERS, null);
		variables.put(CANDIDATE_GROUPS, null);
		variables.put(OK, null);
		variables.put(TYPE, null);
		maxSizeBusinessKey = DEFAULT_MAX_SIZE_BUSINESS_KEY;
    }
    
    //设置BpmnModel
    private BpmnModel bpmnModel = null;
    public void setBpmnModel(BpmnModel bpmnModel) {
    	this.bpmnModel = bpmnModel;
    }
    public BpmnModel getBpmnModel() {
    	return this.bpmnModel;
    }
    
	//设定分支节点
	public void setOk(boolean isOk) {
		variables.put(OK, isOk);
	}
	public void setType(int type) {
		variables.put(TYPE, type);
	}
	public void setComment(String comment) {
		variables.put(COMMENT, comment);
	}

    //处理者key
    public static final String APPLY_USERID = "applyUserId";
	public static final String ASSIGNEE = "assignee";//检查者（用户）
	public static final String CANDIDATE_USERS = "candidateUsers";//检查候选人一览（用户）
	public static final String CANDIDATE_GROUPS = "candidateGroups";//检查候选人组一览（角色）
		
	//设置需要处理的人（用户）
	public void setAssignee(String assignee) {
		if (StringUtil.isEmpty(assignee)) {
			return;
		}
		variables.put(ASSIGNEE, assignee);
	}
	
	//设置需要处理的人(用户组)
	public void setCandidateUsers(List<String> users) {
		if (CollectionUtils.isEmpty(users)) {
			return;
		}
		variables.put(CANDIDATE_USERS, users);
	}
	
	//设置需要处理的人（角色组）
	public void setCandidateGroups(List<String> groups) {
		if (CollectionUtils.isEmpty(groups)) {
			return;
		}
		variables.put(CANDIDATE_GROUPS, groups);
	}
	
	//task一览key
	public static final String BUSINESS_KEY = "businessKey";
	public static final String PROCESS_INSTANCE_ID = "processInstanceId";
	public static final String PROCESS_DEFINITION_ID = "processDefinitionId";
	public static final String TASKS = "tasks";
	public static final String TASK_ID = "taskId";
	public static final String TASK_NAME = "taskName";
	public static final String TASK_ASSIGNEE = "taskAssignee";
	public static final String TASK_GROUP_IDS = "taskGroupIds";
	public static final String TASK_USER_IDS = "taskUserIds";
	public static final String TASK_VARIABLES = "taskVariables";

	//流程变量
	private String userId;//用户ID
	private String businessKey;//业务关联主键，根据这个key可以查看状态
	private String processDefinitionKey;//定义流程图的key,决定到底查看的是哪一个流程图
	private String processInstanceId;//启动流程的ID（每个流程都是唯一的）
	private String processDefinitionId;//启动流程的ID（每个流程都是唯一的）
	private String taskId;//任务	ID
	private String taskName;//任务名称
	private Map<String, Object> variables = new HashMap<String, Object>();//可以传递一些变量参数（用于画面流转时使用）
	private String diagramDir;//流程图保存的硬盘地址
	private String deploymentId;//部署图的ID
	private List<String> usedBusinessKeyList;
	private List<String> usingBusinessKeyList;
	private int maxSizeBusinessKey;//能查询的最大案件数量
	private String deleteReason; // 流程实例删除原因
	
	private InputStream processDiagram;//流程图Stream
	public InputStream getProcessDiagram() {
		return processDiagram;
	}
	public void setProcessDiagram(InputStream processDiagram) {
		this.processDiagram = processDiagram;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariable(String key, Object value) {
		variables.put(key, value);
	}

	public String getDiagramDir() {
		return diagramDir;
	}

	public void setDiagramDir(String diagramDir) {
		this.diagramDir = diagramDir;
	}
	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public List<String> getUsedBusinessKeyList() {
		return usedBusinessKeyList;
	}

	public void setUsedBusinessKeyList(List<String> usedBusinessKeyList) {
		this.usedBusinessKeyList = usedBusinessKeyList;
	}

	public List<String> getUsingBusinessKeyList() {
		return usingBusinessKeyList;
	}

	public void setUsingBusinessKeyList(List<String> usingBusinessKeyList) {
		this.usingBusinessKeyList = usingBusinessKeyList;
	}

	public int getMaxSizeBusinessKey() {
		return maxSizeBusinessKey;
	}

	public void setMaxSizeBusinessKey(int maxSizeBusinessKey) {
		this.maxSizeBusinessKey = maxSizeBusinessKey;
	}

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}
}
