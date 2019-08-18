package com.polaris.workflow.listener;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.polaris.core.config.ConfClient;

/**
 * 流程结束监听器
 *
 * @author: yufenghua
 */
@Component
public class WorkflowProcessEndListener {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    //JpaTransactionManager事务管理 .
    @Resource(name = "transactionManager")
    JpaTransactionManager tm;

    public void onEvent(ActivitiEvent event) {

        //获取processInstanceId
        ActivitiEntityEvent entityEvent = (ActivitiEntityEvent) event;
        String processInstanceId = entityEvent.getProcessInstanceId();

        //判断数据库
        String dialect = ConfClient.get("hibernate.dialect", "");
        if (dialect == null) {
            return;
        }

        //启动线程删除不需要的变量
        DeleteVariablesThread deleteVariablesThread = new DeleteVariablesThread(processInstanceId, dialect, entityManager, tm, logger);
        deleteVariablesThread.start();
    }


    /**
     * 流程完成后清理无用的全局变量
     */
    public class DeleteVariablesThread extends Thread {
        private String processInstanceId;
        private String dialect;
        private EntityManager entityManager;
        private JpaTransactionManager tm;
        private Logger logger;

        public DeleteVariablesThread(String processInstanceId, String dialect, EntityManager entityManager, JpaTransactionManager tm, Logger logger) {
            this.entityManager = entityManager;
            this.processInstanceId = processInstanceId;
            this.dialect = dialect;
            this.tm = tm;
            this.logger = logger;
        }

        @Override
        public void run() {
            try {
                sleep(5000);//5秒后进行删除操作
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage());
                Thread.currentThread().interrupt();
            }

            //事务开始
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setTimeout(30);
            //事务状态
            TransactionStatus status = tm.getTransaction(def);
            try {
                //此处写持久层逻辑
                if (dialect.toLowerCase().contains("mysql")) {
                    deleteVariablesMysql(processInstanceId);
                }
                if (dialect.toLowerCase().contains("oracle")) {
                    deleteVariablesOracle(processInstanceId);
                }
                tm.commit(status);
            } catch (Exception e) {
                logger.error("出现异常，事务回滚", e);
                if (!status.isCompleted()) {
                    tm.rollback(status);
                }
            }
        }

        /**
         * 流程完成后清理无用的全局变量
         *
         * @param processInstanceId
         * @return
         */
        private void deleteVariablesMysql(String processInstanceId) {

            //act_ge_bytearray
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" delete act_ge_bytearray from act_ge_bytearray,act_hi_varinst ");
            sqlBuilder.append(" where act_hi_varinst.BYTEARRAY_ID_ = act_ge_bytearray.ID_ ");
            sqlBuilder.append(" and act_hi_varinst.PROC_INST_ID_= ? ");
            sqlBuilder.append(" and (act_hi_varinst.TASK_ID_ = '' or act_hi_varinst.TASK_ID_ is null) ");
            sqlBuilder.append(" and act_hi_varinst.NAME_ != 'applyUserId' ");
            entityManager.createNativeQuery(sqlBuilder.toString()).setParameter(1, processInstanceId).executeUpdate();

            //act_ge_bytearray
            sqlBuilder = new StringBuilder();
            sqlBuilder.append(" delete from act_hi_varinst ");
            sqlBuilder.append(" where PROC_INST_ID_= ? ");
            sqlBuilder.append(" and (TASK_ID_ = '' or TASK_ID_ is null) ");
            sqlBuilder.append(" and NAME_ != 'applyUserId' ");
            int i = entityManager.createNativeQuery(sqlBuilder.toString()).setParameter(1, processInstanceId).executeUpdate();
            logger.debug("清理了 {} 条历史变量数据", i);

        }

        /**
         * 流程完成后清理无用的全局变量
         *
         * @param processInstanceId
         * @return
         */
        private void deleteVariablesOracle(String processInstanceId) {

            //act_ge_bytearray
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" delete from act_ge_bytearray  ");
            sqlBuilder.append(" where ID_ in (  ");
            sqlBuilder.append(" select BYTEARRAY_ID_ from act_hi_varinst ");
            sqlBuilder.append(" where PROC_INST_ID_= ? ");
            sqlBuilder.append(" and (TASK_ID_ = '' or TASK_ID_ is null) ");
            sqlBuilder.append(" and NAME_ != 'applyUserId' ) ");
            entityManager.createNativeQuery(sqlBuilder.toString()).setParameter(1, processInstanceId).executeUpdate();

            //act_ge_bytearray
            sqlBuilder = new StringBuilder();
            sqlBuilder.append(" delete from act_hi_varinst ");
            sqlBuilder.append(" where PROC_INST_ID_= ? ");
            sqlBuilder.append(" and (TASK_ID_ = '' or TASK_ID_ is null) ");
            sqlBuilder.append(" and NAME_ != 'applyUserId' ");
            int i = entityManager.createNativeQuery(sqlBuilder.toString()).setParameter(1, processInstanceId).executeUpdate();
            logger.debug("清理了 {} 条历史变量数据", i);
        }

    }

}
    

