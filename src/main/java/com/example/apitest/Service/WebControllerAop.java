package com.example.apitest.Service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @Author 宋宗垚
 * @Date 2019/3/27 16:57
 * @Description Aspect切片类
 */

@Aspect
@Component
public class WebControllerAop {
    @Pointcut("execution(* com.example.apitest.Controller.IndexController.*())")
    public void log(){}

    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        System.out.println("11111111111");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        System.out.println(request.getRequestURI());

    }

    @After("log()")
    public void doAfter(){
        System.out.println("3333");
    }


    @Pointcut("execution(* com.example.apitest.Service.MyService.*())")
    public void test(){}

    @Before("test()")
    public void doBeforeTest(){
        System.out.println("doBeforeTest");
    }


}
