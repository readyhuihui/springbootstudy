package com.yonghui.springbootquartz.task;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@DisallowConcurrentExecution
public class Task2 extends QuartzJobBean {
 
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.error("我是task2222 ，我将执行2s钟， 线程名字 == > {} , 现在时间为 == > {}", Thread.currentThread().getId(),new Date());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getId());
        log.error("我是task2222，我已经执行完成了，线程名字 == > {} , 现在时间为 == > {}",Thread.currentThread().getId(),new Date());
    }
}