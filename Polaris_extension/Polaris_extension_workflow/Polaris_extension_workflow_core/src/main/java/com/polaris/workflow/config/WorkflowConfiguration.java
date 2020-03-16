package com.polaris.workflow.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.polaris.container.config.ConfigurationExtension;
import com.polaris.core.config.ConfClient;
import com.polaris.workflow.listener.WorkflowListener;

public class WorkflowConfiguration implements ConfigurationExtension{

	@Override
	public Class<?>[] getConfigurations() {
		return new Class<?>[]{WorkflowConfig.class};
	}
	
	@Configuration
	@ComponentScan("com.polaris.workflow")
	protected static class WorkflowConfig {
		
		@Value("${diagram.activityFontName}")
		private String activityFontName;
		
		@Value("${diagram.labelFontName}")
		private String labelFontName;
		
		@Autowired 
		@Qualifier("dataSource") 
		private DataSource dataSource;
		
		@Bean(name = "hibernateJpaVendorAdapter")
		public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
			return new HibernateJpaVendorAdapter();
		}

		@Bean(name = "entityManagerFactory")
		public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
			LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
			entityManagerFactory.setDataSource(dataSource);
			entityManagerFactory.setJpaVendorAdapter(hibernateJpaVendorAdapter());
			entityManagerFactory.setPackagesToScan();
			Properties jpaProperties = new Properties();
			String dialect = ConfClient.get("hibernate.dialect","org.hibernate.dialect.MySQL5InnoDBDialect");
			jpaProperties.setProperty("hibernate.dialect", dialect);
			jpaProperties.setProperty("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
			jpaProperties.setProperty("hibernate.hbm2ddl.auto", "update");
			entityManagerFactory.setJpaProperties(jpaProperties);
			return entityManagerFactory;
		}
		@Bean(name = "entityManager")
		public EntityManager entityManager() {
			return entityManagerFactory().getObject().createEntityManager();
		}
		
		@Primary
		@Bean(name = "jpaTransactionManager")
		public JpaTransactionManager transactionManager() {
			JpaTransactionManager transactionManager = new JpaTransactionManager();
			transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
			return transactionManager;
		}
		
		@Bean(name = "uuidGenerator")
		public StrongUuidGenerator uuidGenerator() {
			return new StrongUuidGenerator();
		}
		
		@Bean(name="processEngineConfiguration")
		public SpringProcessEngineConfiguration processEngineConfiguration(
				WorkflowListener workflowListener,
				@Autowired @Qualifier("entityManagerFactory")EntityManagerFactory entityManagerFactory,
				@Autowired @Qualifier("jpaTransactionManager") JpaTransactionManager transactionManager,
				@Autowired @Qualifier("uuidGenerator") StrongUuidGenerator uuidGenerator) {
			SpringProcessEngineConfiguration processEngineConfiguration = 
					new SpringProcessEngineConfiguration();
			processEngineConfiguration.setDataSource(dataSource);
			processEngineConfiguration.setTransactionManager(transactionManager);
			processEngineConfiguration.setDatabaseSchemaUpdate("true");
			processEngineConfiguration.setJobExecutorActivate(false);
			processEngineConfiguration.setProcessDefinitionCacheLimit(10);
			processEngineConfiguration.setDbIdentityUsed(false);
			processEngineConfiguration.setIdGenerator(uuidGenerator);
			processEngineConfiguration.setActivityFontName(activityFontName);
			processEngineConfiguration.setLabelFontName(labelFontName);
			processEngineConfiguration.setJpaEntityManagerFactory(entityManagerFactory);
			processEngineConfiguration.setJpaHandleTransaction(false);
			processEngineConfiguration.setJpaCloseEntityManager(false);
			List<ActivitiEventListener> eventListeners = new ArrayList<>();
			eventListeners.add(workflowListener);
			processEngineConfiguration.setEventListeners(eventListeners);
			return processEngineConfiguration;
		}
		
		@Bean(name = "processEngine")
		public ProcessEngine processEngine(
				@Autowired @Qualifier("processEngineConfiguration")SpringProcessEngineConfiguration processEngineConfiguration) throws Exception {
			ProcessEngineFactoryBean processEngine=  new ProcessEngineFactoryBean();
			processEngine.setProcessEngineConfiguration(processEngineConfiguration);
			return processEngine.getObject();
		}
		
		@Bean(name = "repositoryService")
		public RepositoryService repositoryService(
				@Autowired @Qualifier("processEngine")ProcessEngine processEngine) {
			return processEngine.getRepositoryService();
		}
		
		@Bean(name = "runtimeService")
		public RuntimeService runtimeService(
				@Autowired @Qualifier("processEngine")ProcessEngine processEngine) {
			return processEngine.getRuntimeService();
		}
		
		@Bean(name = "formService")
		public FormService formService(
				@Autowired @Qualifier("processEngine")ProcessEngine processEngine) {
			return processEngine.getFormService();
		}
		
		@Bean(name = "identityService")
		public IdentityService identityService(
				@Autowired @Qualifier("processEngine")ProcessEngine processEngine) {
			return processEngine.getIdentityService();
		}
		
		@Bean(name = "taskService")
		public TaskService taskService(
				@Autowired @Qualifier("processEngine")ProcessEngine processEngine) {
			return processEngine.getTaskService();
		}
		
		@Bean(name = "historyService")
		public HistoryService historyService(
				@Autowired @Qualifier("processEngine")ProcessEngine processEngine) {
			return processEngine.getHistoryService();
		}
		
		@Bean(name = "managementService")
		public ManagementService managementService(
				@Autowired @Qualifier("processEngine")ProcessEngine processEngine) {
			return processEngine.getManagementService();
		}

	}

}
