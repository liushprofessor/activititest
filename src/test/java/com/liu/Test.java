package com.liu;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Liush
 * @description
 * @date 2019/3/19 0019 9:42
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class Test {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;

    //查询部署的流程
    @org.junit.Test
    public void searchDeployment(){
        Deployment deployment=repositoryService.createDeploymentQuery().deploymentKey("atest").singleResult();
        System.out.println(deployment);

    }

    //开启流程
    @org.junit.Test
    public void startInstance(){
        identityService.setAuthenticatedUserId("user1");//设置此次流程发起人为user1
        ProcessInstance processInstance=runtimeService.startProcessInstanceByKey("atest");
        System.out.println(processInstance);
    }

    //查询由user1发起的任务，并且查询该任务有多少正在进行的任务，并且完成
    @org.junit.Test
    public void searchInstanceAndCompleteTask(){
        //查询由user1发起的任务
        List<ProcessInstance> instances=runtimeService.createProcessInstanceQuery().startedBy("user1").list();
        //查询正在进行的流程有多少正在进行的任务
        List<Task> tasks=taskService.createTaskQuery().processInstanceId(instances.get(0).getId()).list();
        System.out.println(tasks.get(0).getName());//任务节点的名字
        //将任务指派给user2去完成，这样这个任务就属于user2
        taskService.setAssignee(tasks.get(0).getId(),"user2");
        //查询指派给user2的任务有多少
        List<Task> user2Tasks=taskService.createTaskQuery().taskAssignee("user2").list();
        //完成任务,并且设置流程走向变量
        Map<String,Object> varMap=new HashMap<String, Object>();
        varMap.put("pass",1);
        taskService.complete(user2Tasks.get(0).getId(),varMap);
        //再做一次查询发现现在任务已经到达下一个节点
        List<Task> nextTasks=taskService.createTaskQuery().processInstanceId(instances.get(0).getId()).list();
    }

    //根据流程实例查询任务
    @org.junit.Test
    public void searchTasksByInstanceKey(){
            List<ProcessInstance> instances=runtimeService.createProcessInstanceQuery().processDefinitionKey("atest").list();
            System.out.println(instances);
    }

    //根据流程实例id查询当前流程正在进行的任务
    @org.junit.Test
    public void searchTaskOpeningByInstanceId(){

        List<Task> tasks=taskService.createTaskQuery().processInstanceId("145001").list();

    }

    //完成任务
    @org.junit.Test
    public void completeTask(){
        taskService.complete("150005");


    }


    //发起message事件
    @org.junit.Test
    public void sendMessage(){
        //根据instanceId查询当前正在运行且订阅mes1消息的执行元素
        List<Execution> executionList=runtimeService.createExecutionQuery().
                processInstanceId("145001").
                messageEventSubscriptionName("mes1").list();
        //根据执行id发起消息
        executionList.forEach(execution -> runtimeService.messageEventReceived("mes1",execution.getId()));


    }


}
