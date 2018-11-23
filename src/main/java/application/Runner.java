package application;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试任务流程
 * 开始(myProcess_1) -> 初审(初审人x) -> 复审(复审人y) -> 结束
 *
 * @author suqf
 */
@Slf4j
@Component
public class Runner {

    private String key = "myProcess_1";

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @Bean
    @Order(1)
    public CommandLineRunner start() {
        return args -> {

            Map<String, Object> map = new HashMap<>(2);
            map.put("person", "m");
            map.put("redirect", false);

            ProcessInstance instance = runtimeService.startProcessInstanceByKey(key, map);

            log.error("id = {}, name = {}", instance.getId(), instance.getName());
        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner check() {
        return args -> {

            // 初审人
            String assignee = "x";
            TaskQuery taskQuery = taskService.createTaskQuery();
            List<Task> list = taskQuery.taskAssignee(assignee).list();

            for (Task task : list) {
                log.error("person = {}, assignee = {}, id = {}, name = {}",
                        taskService.getVariable(task.getId(), "person"),
                        task.getAssignee(),
                        task.getId(),
                        task.getName());

                taskService.complete(task.getId());
            }
        };
    }

    @Bean
    @Order(3)
    public CommandLineRunner recheck() {
        return args -> {

            // 复审人
            String assignee = "y";
            TaskQuery taskQuery = taskService.createTaskQuery();
            List<Task> list = taskQuery.taskAssignee(assignee).list();

            for (Task task : list) {
                log.error("assignee = {}, id = {}, name = {}", task.getAssignee(), task.getId(), task.getName());
                taskService.complete(task.getId());
            }
        };
    }

    @Bean
    @Order(4)
    public CommandLineRunner history() {
        return args -> {
            List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();

            for (HistoricProcessInstance instance : list) {
                log.info("id = {}, process id = {}, startTime = {}, endTime = {}",
                        instance.getId(),
                        instance.getProcessDefinitionId(),
                        instance.getStartTime(),
                        instance.getEndTime());
            }
        };
    }
}
