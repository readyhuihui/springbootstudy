package com.yonghui.springbootdevtools.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:lyh
 * @Description:
 * @Date:Created in 2020/8/26 10:09
 */
@RestController
public class HelloController {

    @GetMapping("v1/sayHello")
    public String sayHello() {

        return "hello";
    }
}
