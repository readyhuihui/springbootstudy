package com.yonghui.springbootmybatisgenerator;

//import org.mybatis.spring.annotation.MapperScan;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.yonghui.springbootmybatisgenerator.mapper")
public class SpringBootMybatisGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMybatisGeneratorApplication.class, args);
    }

}
