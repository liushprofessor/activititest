package com.liu;


import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author Liush
 * @description
 * @date 2019/3/19 0019 10:21
 **/
public class MyListener  implements TaskListener{


    public void notify(DelegateTask delegateTask) {
        System.out.println("任务已经被完成");
    }
}
