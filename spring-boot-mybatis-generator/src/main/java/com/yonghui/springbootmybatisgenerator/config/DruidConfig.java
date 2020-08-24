package com.yonghui.springbootmybatisgenerator.config;

import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class DruidConfig {
    @Bean(name = "druid-stat-interceptor")
    public DruidStatInterceptor druidStatInterceptor() {
        return new DruidStatInterceptor();
    }

    @Bean(name = "druid-stat-pointcut")
    // 非单例
    @Scope("prototype")
    public JdkRegexpMethodPointcut druidStatPointcut() {
        final JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
        // 这里是你Controller包的路径
        pointcut.setPatterns("com.yonghui.springbootmybatisgenerator.web.*", "com.yonghui.springbootmybatisgenerator.service.*","com.yonghui.springbootmybatisgenerator.dao.mapper.*");
        return pointcut;
    }

    @Bean
    public DefaultPointcutAdvisor druidStatAdvisor(@Qualifier("druid-stat-interceptor") final DruidStatInterceptor druidStatInterceptor,
                                                   @Qualifier("druid-stat-pointcut") final JdkRegexpMethodPointcut druidStatPointcut) {
        final DefaultPointcutAdvisor defaultPointAdvisor = new DefaultPointcutAdvisor();
        defaultPointAdvisor.setPointcut(druidStatPointcut);
        defaultPointAdvisor.setAdvice(druidStatInterceptor);
        return defaultPointAdvisor;
    }
}