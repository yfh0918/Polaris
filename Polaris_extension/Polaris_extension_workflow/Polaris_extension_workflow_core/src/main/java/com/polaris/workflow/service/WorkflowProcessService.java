package com.polaris.workflow.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.polaris.core.Constant;
import com.polaris.core.pojo.Page;
import com.polaris.core.util.PageUtil;
import com.polaris.core.util.StringUtil;
import com.polaris.workflow.dto.WorkflowDto;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

@Component
@Transactional
public class WorkflowProcessService {

    //日志
    private static Logger logger = LoggerFactory.getLogger(WorkflowProcessService.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @PersistenceContext
    private EntityManager entityManager;
    


    /**
     * 启动流程
     *
     * @param entity
     */
    @Transactional
    public WorkflowDto startWorkflow(WorkflowDto dto) {

        //参数检查
        if (dto == null) {
            dto = new WorkflowDto();
        }
        if (StringUtil.isEmpty(dto.getUserId())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[0]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }
        if (StringUtil.isEmpty(dto.getProcessDefinitionKey())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[1]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }
        if (StringUtil.isEmpty(dto.getBusinessKey())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[2]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }
        if (dto.getVariables() == null) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[3]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }

        //检查该businessKey是否使用中或者使用过
        if (!businessKeyValidate(dto)) {
            return dto;
        }

        //启动流程
        ProcessInstance processInstance = null;
        try {
            // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
            identityService.setAuthenticatedUserId(dto.getUserId());
            processInstance = runtimeService.startProcessInstanceByKey(dto.getProcessDefinitionKey(), dto.getBusinessKey(), dto.getVariables());
            String processInstanceId = processInstance.getId();
            dto.setProcessInstanceId(processInstanceId);
            logger.debug("start process of {key={}, bkey={}, pid={}, variables={}}",
                    new Object[]{dto.getProcessDefinitionKey(), dto.getBusinessKey(), processInstanceId, dto.getVariables()});
            dto.setProcessDefinitionId(processInstance.getProcessDefinitionId());
            dto.setCode(Constant.RESULT_SUCCESS);
        } catch (Exception ex) {
            logger.error("异常", ex);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            dto.setMessage(ex.getMessage());
        } finally {
            identityService.setAuthenticatedUserId(null);
        }
        return dto;
    }

    /**
     * 查询待办任务
     *
     * @param userId 用户ID
     * @return
     */
    @SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
    public WorkflowDto findTodoTasks(WorkflowDto dto) {

        //参数检查
        if (dto == null) {
            dto = new WorkflowDto();
        }
        if (StringUtil.isEmpty(dto.getUserId())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[0]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }
        if (StringUtil.isEmpty(dto.getProcessDefinitionKey())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[1]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }

        try {
            //结果List
            List<Map<String, Object>> datas = new ArrayList<>();

            //分页
            Page<WorkflowDto> page = new Page<WorkflowDto>(dto.getPageSize(),dto.getPageIndex());
            int[] pageParams = PageUtil.init(page);

            // 根据当前人的ID查询
            TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey(dto.getProcessDefinitionKey()).taskCandidateOrAssigned(dto.getUserId());
            if (StringUtil.isNotEmpty(dto.getProcessInstanceId())) {
            	taskQuery = taskQuery.processInstanceId(dto.getProcessInstanceId());
            }
            if (StringUtil.isNotEmpty(dto.getTaskId())) {
            	taskQuery = taskQuery.taskId(dto.getTaskId());
            }
            if (StringUtil.isNotEmpty(dto.getTaskDefinitionKey())) {
            	taskQuery = taskQuery.taskDefinitionKey(dto.getTaskDefinitionKey());
            }
            
            List<Task> tasks = taskQuery.orderByTaskCreateTime().asc().listPage(pageParams[0], pageParams[1]);

            // 根据流程的业务ID查询实体并关联
            for (Task task : tasks) {
                Map<String, Object> map = new HashMap<>();
                String processInstanceId = task.getProcessInstanceId();
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).active().singleResult();
                String businessKey = processInstance.getBusinessKey();//案件编号
                map.put(WorkflowDto.BUSINESS_KEY, businessKey);
                map.put(WorkflowDto.PROCESS_INSTANCE_ID, processInstanceId);
                map.put(WorkflowDto.PROCESS_DEFINITION_ID, processInstance.getProcessDefinitionId());

                //获取全局的APPLY_USERID
                map.put(WorkflowDto.APPLY_USERID, taskService.getVariable(task.getId(), WorkflowDto.APPLY_USERID));
                map.put(WorkflowDto.TASK_ID, task.getId());
                map.put(WorkflowDto.TASK_DEFINITION_KEY, task.getTaskDefinitionKey());
                map.put(WorkflowDto.TASK_NAME, task.getName());
                map.put(WorkflowDto.TASK_ASSIGNEE, task.getAssignee());
                map.put(WorkflowDto.TASK_VARIABLES, taskService.getVariables(task.getId()));
                datas.add(map);
            }
            dto.setTotal((int) taskQuery.count());
            dto.setData(datas);
            dto.setCode(Constant.RESULT_SUCCESS);
        } catch (Exception ex) {
            logger.error("异常", ex);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            dto.setMessage(ex.getMessage());
        }
        return dto;
    }

    /**
     * 任务认领
     *
     * @param userId 用户ID
     * @return
     */
    @Transactional
    public WorkflowDto claim(WorkflowDto dto) {

        //参数检查
        if (dto == null) {
            dto = new WorkflowDto();
        }
        if (StringUtil.isEmpty(dto.getUserId())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[0]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }
        if (StringUtil.isEmpty(dto.getTaskId())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[4]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }

        //领取任务
        try {
            taskService.claim(dto.getTaskId(), dto.getUserId());
            dto.setCode(Constant.RESULT_SUCCESS);
        } catch (Exception ex) {
            logger.error("异常", ex);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            dto.setMessage(ex.getMessage());
        }
        return dto;
    }

    /**
     * 处理任务
     *
     * @param userId 用户ID
     * @return
     */
    @Transactional
    public WorkflowDto complete(WorkflowDto dto) {

        //参数检查
        if (dto == null) {
            dto = new WorkflowDto();
        }
        if (StringUtil.isEmpty(dto.getTaskId())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[4]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }

        try {
            //完成任务（流程继续往前或者回退）
            if (dto.getVariables() == null) {
                taskService.complete(dto.getTaskId());
            } else {


                //局部变量保存
                Map<String, Object> tempVariables = new HashMap<>();
                Iterator<Map.Entry<String, Object>> it = dto.getVariables().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Object> entry = it.next();
                    String key = entry.getKey();

                    //空值不保存，节约数据库资源
                    if (dto.getVariables().get(key) != null && StringUtil.isNotEmpty(dto.getVariables().get(key).toString())) {
                        tempVariables.put(key, dto.getVariables().get(key));
                    }
                }
                taskService.setVariablesLocal(dto.getTaskId(), tempVariables);

                //全局变量需要保存
                taskService.complete(dto.getTaskId(), dto.getVariables());
            }

            //返回成功变量
            dto.setCode(Constant.RESULT_SUCCESS);
        } catch (Exception ex) {
            logger.error("异常", ex);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            dto.setMessage(ex.getMessage());
        }

        return dto;
    }

    /**
     * 读取运行中的流程
     *
     * @return
     */
    @SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
    public WorkflowDto findRunningProcessInstaces(WorkflowDto dto) {

        //参数检查
        if (dto == null) {
            dto = new WorkflowDto();
        }
        if (StringUtil.isEmpty(dto.getProcessDefinitionKey())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[1]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }

        try {
            //结果List
            List<Map<String, Object>> datas = new ArrayList<>();

            //分页
            Page<WorkflowDto> page = new Page<WorkflowDto>(dto.getPageSize(),dto.getPageIndex());
            int[] pageParams = PageUtil.init(page);

            //查询
            ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().processDefinitionKey(dto.getProcessDefinitionKey());
            if (!StringUtil.isEmpty(dto.getUserId())) {
                query = query.involvedUser(dto.getUserId());
            }
            if (!StringUtil.isEmpty(dto.getBusinessKey())) {
                query = query.processInstanceBusinessKey(dto.getBusinessKey());
            }
            List<ProcessInstance> list = query.active().listPage(pageParams[0], pageParams[1]);

            // 查询所有运行中的流程
            for (ProcessInstance processInstance : list) {
                String businessKey = processInstance.getBusinessKey();

                // 流程的信息
                Map<String, Object> processMap = new HashMap<>();
                processMap.put(WorkflowDto.BUSINESS_KEY, businessKey);
                processMap.put(WorkflowDto.PROCESS_INSTANCE_ID, processInstance.getId());
                processMap.put(WorkflowDto.PROCESS_DEFINITION_ID, processInstance.getProcessDefinitionId());

                //所有的任务
                List<Map<String, Object>> tasks = new ArrayList<>();

                //已经结束的任务
                List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstance.getId())
                        .orderByTaskCreateTime().asc().list();
                if (hisTaskList != null && !hisTaskList.isEmpty()) {
                    for (HistoricTaskInstance task : hisTaskList) {
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put(WorkflowDto.TASK_ID, task.getId());
                        taskMap.put(WorkflowDto.TASK_NAME, task.getName());
                        taskMap.put(WorkflowDto.TASK_ASSIGNEE, task.getAssignee());

                        //获取groups和users
                        getTaskGroupsAndUsers(taskMap, "act_hi_identitylink", task.getId());

                        //获取变量
                        Map<String, Object> variableMap = new HashMap<>();
                        getHisVariableList(processInstance.getId(), task.getId(), variableMap);
                        taskMap.put(WorkflowDto.TASK_VARIABLES, variableMap);

                        //添加到tasks中
                        addTaskMap(tasks, taskMap, task.getId());
                    }
                }

                // 还在运行中的任务
                List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().orderByTaskCreateTime().asc().list();
                if (taskList != null && !taskList.isEmpty()) {
                    for (Task task : taskList) {
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put(WorkflowDto.TASK_ID, task.getId());
                        taskMap.put(WorkflowDto.TASK_NAME, task.getName());
                        taskMap.put(WorkflowDto.TASK_ASSIGNEE, task.getAssignee());

                        //获取groups和users
                        getTaskGroupsAndUsers(taskMap, "act_ru_identitylink", task.getId());

                        //获取全局的APPLY_USERID
                        if (processMap.get(WorkflowDto.APPLY_USERID) == null ||
                                StringUtil.isEmpty(processMap.get(WorkflowDto.APPLY_USERID).toString())) {
                            processMap.put(WorkflowDto.APPLY_USERID, taskService.getVariable(task.getId(), WorkflowDto.APPLY_USERID));
                        }

                        //获取变量
                        taskMap.put(WorkflowDto.TASK_VARIABLES, taskService.getVariablesLocal(task.getId()));

                        //添加到tasks中
                        addTaskMap(tasks, taskMap, task.getId());
                    }
                }
                processMap.put(WorkflowDto.TASKS, tasks);
                datas.add(processMap);
            }
            dto.setTotal((int) query.count());
            dto.setData(datas);
            dto.setCode(Constant.RESULT_SUCCESS);
        } catch (Exception ex) {
            logger.error("异常", ex);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            dto.setMessage(ex.getMessage());
        }
        return dto;
    }

    /**
     * 读取已结束中的流程
     *
     * @return
     */
    @SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
    public WorkflowDto findFinishedProcessInstaces(WorkflowDto dto) {

        //参数检查
        if (dto == null) {
            dto = new WorkflowDto();
        }
        if (StringUtil.isEmpty(dto.getProcessDefinitionKey())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[1]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }

        try {
            //结果List
            List<Map<String, Object>> datas = new ArrayList<>();

            //分页
            Page<WorkflowDto> page = new Page<WorkflowDto>(dto.getPageSize(),dto.getPageIndex());
            int[] pageParams = PageUtil.init(page);

            //查询流程
            HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(dto.getProcessDefinitionKey());
            if (!StringUtil.isEmpty(dto.getUserId())) {
                query = query.involvedUser(dto.getUserId());
            }
            if (!StringUtil.isEmpty(dto.getBusinessKey())) {
                query = query.processInstanceBusinessKey(dto.getBusinessKey());
            }
            query = query.finished().orderByProcessInstanceEndTime().asc();
            List<HistoricProcessInstance> list = query.listPage(pageParams[0], pageParams[1]);

            // 查询所有结束的流程
            for (HistoricProcessInstance historicProcessInstance : list) {
                String businessKey = historicProcessInstance.getBusinessKey();

                // 流程的信息
                Map<String, Object> processMap = new HashMap<>();
                processMap.put(WorkflowDto.BUSINESS_KEY, businessKey);
                processMap.put(WorkflowDto.PROCESS_INSTANCE_ID, historicProcessInstance.getId());
                processMap.put(WorkflowDto.PROCESS_DEFINITION_ID, historicProcessInstance.getProcessDefinitionId());

                //获取全局的APPLY_USERID
                processMap.put(WorkflowDto.APPLY_USERID, getApplyUserId(historicProcessInstance.getId()));

                // 流程下所有任务信息
                List<Map<String, Object>> tasks = new ArrayList<>();
                List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(historicProcessInstance.getId())
                        .orderByTaskCreateTime().asc().list();
                if (hisTaskList != null && !hisTaskList.isEmpty()) {
                    for (HistoricTaskInstance task : hisTaskList) {
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put(WorkflowDto.TASK_ID, task.getId());
                        taskMap.put(WorkflowDto.TASK_NAME, task.getName());
                        taskMap.put(WorkflowDto.TASK_ASSIGNEE, task.getAssignee());

                        //获取groups和users
                        getTaskGroupsAndUsers(taskMap, "act_hi_identitylink", task.getId());

                        //获取变量
                        Map<String, Object> variableMap = new HashMap<>();
                        getHisVariableList(historicProcessInstance.getId(), task.getId(), variableMap);
                        taskMap.put(WorkflowDto.TASK_VARIABLES, variableMap);

                        //添加到tasks中
                        addTaskMap(tasks, taskMap, task.getId());
                    }
                }
                processMap.put(WorkflowDto.TASKS, tasks);
                datas.add(processMap);
            }
            dto.setTotal((int) query.count());
            dto.setData(datas);
            dto.setCode(Constant.RESULT_SUCCESS);
        } catch (Exception ex) {
            logger.error("异常", ex);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            dto.setMessage(ex.getMessage());
        }
        return dto;
    }

    /**
     * 删除没有结束的流程
     *
     * @return
     */
    @Transactional
    public WorkflowDto deleteRuntimeProcessInstance(String processInstanceId, String deleteReason) {
        // 参数检查
    	WorkflowDto dto = new WorkflowDto();
        if (StringUtil.isEmpty(processInstanceId)) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[6]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }
        // 删除流程
        try {
            runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
            dto.setCode(Constant.RESULT_SUCCESS);
        } catch (Exception ex) {
            logger.error("异常", ex);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            dto.setMessage(ex.getMessage());
        }
        return dto;
    }

    /**
     * 删除已经结束的流程
     *
     * @return
     */
    @Transactional
    public WorkflowDto deleteFinishedProcessInstaces(String processInstanceId) {

        //参数检查
    	WorkflowDto dto = new WorkflowDto();
        if (StringUtil.isEmpty(processInstanceId)) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[6]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }

        //删除流程
        try {
            historyService.deleteHistoricProcessInstance(processInstanceId);
            dto.setCode(Constant.RESULT_SUCCESS);
        } catch (Exception ex) {
            logger.error("异常", ex);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            dto.setMessage(ex.getMessage());
        }
        return dto;
    }

    /**
     * 查询所有使用过的和未使用的businessKey
     *
     * @return
     */
    @Transactional(readOnly = true)
    public WorkflowDto getBusinessKeyList(WorkflowDto dto) {

        //参数检查
        if (dto == null) {
            dto = new WorkflowDto();
        }
        if (StringUtil.isEmpty(dto.getProcessDefinitionKey())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[1]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }

        try {
            //查询正在使用中的案件
            int maxResults = dto.getMaxSizeBusinessKey() + 1;
            List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().
                    processDefinitionKey(dto.getProcessDefinitionKey()).active().listPage(0, maxResults);
            if (processInstanceList != null && !processInstanceList.isEmpty()) {
                List<String> businessKeySet = new ArrayList<>();
                for (ProcessInstance processInstance : processInstanceList) {
                    if (processInstance != null && StringUtil.isNotEmpty(processInstance.getBusinessKey())) {
                        businessKeySet.add(processInstance.getBusinessKey());
                    }
                }
                dto.setUsingBusinessKeyList(businessKeySet);
                if (businessKeySet.size() == maxResults) {
                    businessKeySet.remove(dto.getMaxSizeBusinessKey());
                    dto.setMessage(WorkflowDto.MESSAGE_INFO[9] + dto.getMaxSizeBusinessKey());
                }
            }

            //查询使用过的案件
            List<HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().
                    processDefinitionKey(dto.getProcessDefinitionKey()).orderByProcessInstanceStartTime().desc().finished().listPage(0, maxResults);
            if (historicProcessInstanceList != null && !historicProcessInstanceList.isEmpty()) {
                List<String> businessKeySet = new ArrayList<>();
                for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
                    if (historicProcessInstance != null && StringUtil.isNotEmpty(historicProcessInstance.getBusinessKey())) {
                        businessKeySet.add(historicProcessInstance.getBusinessKey());
                    }
                }
                dto.setUsedBusinessKeyList(businessKeySet);
                if (businessKeySet.size() == maxResults) {
                    businessKeySet.remove(dto.getMaxSizeBusinessKey());
                    if (StringUtil.isNotEmpty(dto.getMessage())) {
                        dto.setMessage(dto.getMessage() + ";" + WorkflowDto.MESSAGE_INFO[10] + dto.getMaxSizeBusinessKey());
                    } else {
                        dto.setMessage(WorkflowDto.MESSAGE_INFO[10] + dto.getMaxSizeBusinessKey());
                    }
                }
            }
            dto.setCode(Constant.RESULT_SUCCESS);
        } catch (Exception ex) {
            logger.error("异常", ex);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            dto.setMessage(ex.getMessage());
        }
        return dto;
    }

    /**
     * 获取ApplyUserId
     *
     * @return
     */
    private String getApplyUserId(String historicProcessInstanceId) {
        List<HistoricVariableInstance> hisVariableList = historyService.createHistoricVariableInstanceQuery().
                processInstanceId(historicProcessInstanceId).list();
        if (hisVariableList != null && !hisVariableList.isEmpty()) {
            for (HistoricVariableInstance variable : hisVariableList) {
                if (variable.getVariableName() != null && variable.getVariableName().equals(WorkflowDto.APPLY_USERID)) {
                    if (variable.getValue() != null) {
                        return variable.getValue().toString();
                    }
                    break;
                }
            }
        }
        return null;
    }

    /**
     * 获取历史变量
     *
     * @return
     */
    private void getHisVariableList(String processInstanceId, String taskId, Map<String, Object> variableMap) {
        List<HistoricVariableInstance> hisVariableList = historyService.createHistoricVariableInstanceQuery().
                processInstanceId(processInstanceId).taskId(taskId).list();
        if (hisVariableList != null && !hisVariableList.isEmpty()) {
            for (HistoricVariableInstance variable : hisVariableList) {
                if (StringUtil.isNotEmpty(variable.getTaskId())) {
                    variableMap.put(variable.getVariableName(), variable.getValue());
                }
            }
        }
    }

    /**
     * map添加到task列表中
     *
     * @return
     */
    private void addTaskMap(List<Map<String, Object>> tasks, Map<String, Object> taskMap, String taskId) {
        //删除已经存在的taskid
        for (Map<String, Object> tempMap : tasks) {
            if (tempMap.get(WorkflowDto.TASK_ID).equals(taskId)) {
                tasks.remove(tempMap);
                break;
            }
        }
        tasks.add(taskMap);
    }

    /**
     * 获取TASK_GROUP_IDS和TASK_USER_IDS
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    private void getTaskGroupsAndUsers(Map<String, Object> taskMap, String table, String taskId) {

        List<String> groupIds = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select GROUP_ID_,USER_ID_ from " + table + " where TASK_ID_ = :taskId ");
        //执行
        List<Object[]> result = entityManager.createNativeQuery(sqlBuilder.toString()).setParameter("taskId", taskId).getResultList();
        if (result != null) {
            for (Object[] obj : result) {
                if (obj[0] != null && StringUtil.isNotEmpty(obj[0].toString())) {
                    groupIds.add(obj[0].toString());
                }
                if (obj[1] != null && StringUtil.isNotEmpty(obj[1].toString())) {
                    userIds.add(obj[1].toString());
                }
            }
        }
        if (!groupIds.isEmpty()) {
            taskMap.put(WorkflowDto.TASK_GROUP_IDS, groupIds);
        }
        if (!userIds.isEmpty()) {
            taskMap.put(WorkflowDto.TASK_USER_IDS, userIds);
        }
    }

    /**
     * 检查该businessKey是否已经使用或者已经使用过了
     *
     * @return
     */
    private boolean businessKeyValidate(WorkflowDto dto) {

        //查询正在使用中的案件
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().
                processDefinitionKey(dto.getProcessDefinitionKey()).
                processInstanceBusinessKey(dto.getBusinessKey()).active().singleResult();
        if (processInstance != null && StringUtil.isNotEmpty(processInstance.getBusinessKey())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[7] + "{" + processInstance.getBusinessKey() + "}");
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return false;
        }

        //查询使用过的案件
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().
                processDefinitionKey(dto.getProcessDefinitionKey()).
                processInstanceBusinessKey(dto.getProcessDefinitionKey()).finished().singleResult();
        if (historicProcessInstance != null && StringUtil.isNotEmpty(historicProcessInstance.getBusinessKey())) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[8] + "{" + historicProcessInstance.getBusinessKey() + "}");
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return false;
        }
        return true;
    }

    /**
     * 获取下一个用户任务用户组信息
     *
     * @param taskId      任务Id信息
     * @param gateWayCond 工作流的网关判断条件
     * @return 下一个用户任务用户组信息
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public WorkflowDto getNextTaskGroup(WorkflowDto dto) {
        //获取任务号 和网关判断条件
        String taskId = dto.getTaskId();
        List<Map<String, Object>> gateWayCond = (List<Map<String, Object>>)dto.getData();
        if (StrUtil.isEmpty(taskId)) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[4]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }
        ProcessDefinitionEntity processDefinitionEntity;

        //获取流程实例Id信息
        Task taskResult = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = taskResult.getProcessInstanceId();
        String executionId = taskResult.getExecutionId();

        //获取流程发布Id信息
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();

        processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(definitionId);

        ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(executionId).processInstanceId(processInstanceId).singleResult();

        //当前流程节点Id信息
        String activitiId = execution.getActivityId();

        //获取流程所有节点信息
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();

        //遍历所有节点信息
        List<ActivityImpl> collect = activitiList.stream().filter(activityImpl -> {
            if (Objects.equals(activityImpl.getId(), activitiId)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(collect)) {
            dto.setMessage(WorkflowDto.MESSAGE_INFO[4]);
            dto.setCode(String.valueOf(Constant.RESULT_FAIL));
            return dto;
        }
        //查找下个节点
        List<TaskDefinition> taskList = new ArrayList<>();
        nextTaskDefinition(taskList, collect.get(0), collect.get(0).getId(), gateWayCond, processInstanceId);
        Map<String, Object> resultMap = new HashMap<>();
        for (TaskDefinition task : taskList) {
            //key为节点id，value为节点中文描述
            resultMap.put(task.getKey(), task.getNameExpression().getExpressionText());
        }
        dto.setData(resultMap);
        dto.setCode(Constant.RESULT_SUCCESS);
        return dto;
    }

    /**
     * 下一个任务节点信息,
     * <p>
     * 如果下一个节点为用户任务则直接返回,
     * <p>
     * 如果下一个节点为排他网关, 获取排他网关Id信息, 根据排他网关Id信息和execution获取流程实例排他网关Id为key的变量值,
     * 根据变量值分别执行排他网关后线路中的el表达式, 并找到el表达式通过的线路后的用户任务信息
     *
     * @param activityImpl      流程节点信息
     * @param activityId        当前流程节点Id信息
     * @param gateWayCond       网关的判断条件
     * @param processInstanceId 流程实例Id信息
     * @return
     */
    private void nextTaskDefinition(List<TaskDefinition> taskList, ActivityImpl activityImpl, String activityId, List<Map<String, Object>> gateWayCond, String processInstanceId) {
        PvmActivity ac = null;
        Object s = null;
        //如果遍历节点为用户任务并且节点不是当前节点信息
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            //获取该节点下一个节点信息
            TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior()).getTaskDefinition();
            taskList.add(taskDefinition);
            return;
        } else {
            //获取节点所有流向线路信息
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for (PvmTransition tr : outTransitions) {
                ac = tr.getDestination(); //获取线路的终点节点
                //如果流向线路为排他网关
                if ("exclusiveGateway".equalsIgnoreCase(ac.getProperty("type").toString())) {
                    outTransitionsTemp = ac.getOutgoingTransitions();
                    //如果排他网关只有一条线路信息
                    if (outTransitionsTemp.size() == 1) {
                    	nextTaskDefinition(taskList, (ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId, gateWayCond, processInstanceId);
                    	return;
                    } else if (outTransitionsTemp.size() > 1) {  //如果排他网关有多条线路信息
                        for (PvmTransition tr1 : outTransitionsTemp) {
                            s = tr1.getProperty("conditionText");  //获取排他网关线路判断条件信息
                            //判断网关路由
                            if (CollectionUtil.isEmpty(gateWayCond)) {
                                logger.info(StrUtil.format("网关判断需要的条件不存在，网关名称L{}", ac.getId()));
                                return;
                            }
                            if (isCondition(StrUtil.trim(s.toString()), gateWayCond.get(0))) {
                                //条件集合去除第一个
                                gateWayCond.remove(0);
                                nextTaskDefinition(taskList,(ActivityImpl) tr1.getDestination(), activityId, gateWayCond, processInstanceId);
                                return;
                            }
                        }
                    }
                } else if ("userTask".equalsIgnoreCase(ac.getProperty("type").toString())) {
                    taskList.add(((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior()).getTaskDefinition());
                    return;
                    
                //并行网关
                } else if ("parallelGateway".equalsIgnoreCase(ac.getProperty("type").toString())) {
                	outTransitionsTemp = ac.getOutgoingTransitions();
                	for (PvmTransition tracition : outTransitionsTemp) {
                    	nextTaskDefinition(taskList, (ActivityImpl) tracition.getDestination(), activityId, gateWayCond, processInstanceId);
                	}
                	return;
                } else if ("endEvent".equalsIgnoreCase(ac.getProperty("type").toString())){
                	return;
                } else {
                	return;
                }
            }
            return;
        }
    }

    /**
     * 根据key和value判断el表达式是否通过信息
     *
     * @param el        el表达式信息
     * @param condition 存放key和value
     * @return
     */
    private boolean isCondition(String el, Map<String, Object> condition) {
        if (CollectionUtil.isEmpty(condition)) {
            return false;
        }
        //获取数据和值
        String key = condition.keySet().stream().findFirst().get();
        String value = condition.get(key) + StrUtil.EMPTY;

        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        context.setVariable(key, factory.createValueExpression(value, String.class));
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);
        return (Boolean) e.getValue(context);
    }

    /**
     * 获取所有的用户节点名称
     *
     * @param processDefinitionId 图Id
     * @return 所有用户任务用户名称
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public WorkflowDto getAllUserTask(WorkflowDto dto) {
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentName(dto.getProcessDefinitionKey()).singleResult();
        String deploymentId = deployment.getId();
        String resourceName = dto.getProcessDefinitionKey() + WorkflowCreateService.suffix;
        InputStream resouceStream = repositoryService.getResourceAsStream(deploymentId, resourceName);
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = null;
        XMLStreamReader xtr = null;
        try {
            in = new InputStreamReader(resouceStream, "UTF-8");
            xtr = xif.createXMLStreamReader(in);
            BpmnModel model = new BpmnXMLConverter().convertToBpmnModel(xtr);
            Map<String, Object> resultMap = new LinkedHashMap<>();
            Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
            for (FlowElement flowE : flowElements) {
                if (flowE.getClass() == UserTask.class) {
                    resultMap.put(flowE.getId(), flowE.getName());
                }
            }
            dto.setData(resultMap);
        } catch (XMLStreamException e) {
            logger.error(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        } finally {
        	if (in != null) {
        		try {
					in.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
        	}
        	if (xtr != null) {
        		try {
					xtr.close();
				} catch (XMLStreamException e) {
					logger.error(e.getMessage());
				}
        	}
        }
        return dto;
    }



}
