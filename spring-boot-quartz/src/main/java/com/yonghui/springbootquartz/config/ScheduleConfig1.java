package com.yonghui.springbootquartz.config;

import com.yonghui.springbootquartz.task.Task1;
import com.yonghui.springbootquartz.task.Task2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfig1 {
    protected static final Level OPERATING = Level.forName("BUS", 250);
    private static final Logger log = LogManager.getLogger();
 
 
    @Bean
 
    public JobDetail task1JobDetail() {
        return JobBuilder.newJob(Task1.class)
                .withIdentity("task1")
                .storeDurably(true)
                .build();
    }
 
    @Bean
    public JobDetail task2JobDetail() {
        return JobBuilder.newJob(Task2.class)
                .withIdentity("task2")
                .storeDurably(true)
                .build();
    }
 
    @Bean
    public Trigger task1Trigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("*/4 * * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(task1JobDetail())
                .withIdentity("task1")
                .withSchedule(scheduleBuilder)
                .build();
    }
 
    @Bean
    public Trigger task2Trigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("*/4 * * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(task2JobDetail())
                .withIdentity("task2")
                .withSchedule(scheduleBuilder)
                .build();
    }
}