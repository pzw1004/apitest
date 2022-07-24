package com.example.apitest.Controller;

import com.example.apitest.Service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 宋宗垚
 * @Date 2019/1/17 10:18
 * @Description TODO
 */
@RestController
public class IndexController {

    @Autowired
    private MyService myService;

    @GetMapping("/index")
    public String getIndex(){

        int x = 1;
        myService.tttt();


        System.out.println("2222222222");
        return "index";

    }
}
