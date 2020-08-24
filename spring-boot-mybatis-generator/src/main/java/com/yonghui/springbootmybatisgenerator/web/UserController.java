package com.yonghui.springbootmybatisgenerator.web;

import com.alibaba.fastjson.JSON;
import com.yonghui.springbootmybatisgenerator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author:lyh
 * @Description:
 * @Date:Created in 2020/8/23 19:21
 */
@Controller
@RequestMapping(value = "api/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "v1/getAllUser")
    @ResponseBody
    public String getAllUser() {
        return JSON.toJSONString(userService.selectByExample());
    }

    @RequestMapping(value = "v1/getUserById")
    @ResponseBody
    public String getUserById() {
        return JSON.toJSONString(userService.selectByPrimaryKey(12));
    }

    @RequestMapping(value = "v1/getUserByName")
    @ResponseBody
    public String getUserById(@RequestParam("name") String name) {
        return JSON.toJSONString(userService.findUserByName(name));
    }

}
