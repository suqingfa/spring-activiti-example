package application;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.activiti.engine.*;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author suqf
 */
@Configuration
public class ActivitiConfig {

    @Bean
    public DataSource dataSource(){
        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setURL("jdbc:mysql://root:root@linux/root");

        return dataSource;
    }

    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration(
            DataSource dataSource,
            PlatformTransactionManager transactionManager,
            ApplicationContext context) throws IOException {
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();

        configuration.setDataSource(dataSource);
        configuration.setTransactionManager(transactionManager);
        configuration.setDatabaseSchemaUpdate("create-drop");

        configuration.setDeploymentResources(context.getResources("classpath*:processes/**"));

        return configuration;
    }

    @Bean
    public ProcessEngineFactoryBean processEngineFactoryBean(
            SpringProcessEngineConfiguration configuration) {
        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();

        processEngineFactoryBean.setProcessEngineConfiguration(configuration);

        return processEngineFactoryBean;
    }

    /**
     * 提供与流程定义相关的方法
     * 查询模型(model)、流程定义(process definition)、流程部署(deployment)
     */
    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    /**
     * 提供流程执行时相关的方法
     * 查询流程实例(process insatnce)、执行实例(execution)，可开启流程实例
     */
    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    /**
     * 提供任务相关的方法
     * 进行查询、指派、完成任务等操作
     */
    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    /**
     * 提供历史记录相关的方法
     * 查询历史任务(historic task instance)，历史流程实例(historic process instance)等
     */
    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    /**
     * 与引擎配置相关
     * 获取引擎数据库信息，并且可以执行自定义的命令(command)
     */
    @Bean
    public ManagementService managementService(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }

    /**
     * 提供动态获取，以及动态修改流程定义的方法
     */
    @Bean
    public DynamicBpmnService dynamicBpmnService(ProcessEngine processEngine) {
        return processEngine.getDynamicBpmnService();
    }
}
