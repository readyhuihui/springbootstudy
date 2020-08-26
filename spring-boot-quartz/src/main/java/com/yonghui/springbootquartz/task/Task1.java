package com.yonghui.springbootquartz.task;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@DisallowConcurrentExecution
public class Task1 extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.error("我是task1111 ，我将执行10s钟， 线程名字 == > {} , 现在时间为 == > {}", Thread.currentThread().getId(),new Date());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.error("我是task1111，我已经执行完成了，线程名字 == > {} , 现在时间为 == > {}",Thread.currentThread().getId(),new Date());
    }
}