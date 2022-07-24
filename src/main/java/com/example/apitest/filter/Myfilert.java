package com.example.apitest.filter;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName="myFilter",urlPatterns="/*")
public class Myfilert implements Filter {
    public void destroy() {
        System.out.println("过滤器销毁");
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse)response;
//    	HttpServletRequest req = (HttpServletRequest)request;
//    	String ss =req.getMethod();
//    	System.out.println("-----------------------跨域处理-------------------------------");
//    	System.out.println(ss);
        //"*"存在风险，建议指定可信任的域名来接收响应信息，如"http://www.sosoapi.com"
        resp.addHeader("Access-Control-Allow-Origin", "*");
        //如果存在自定义的header参数，需要在此处添加，逗号分隔
        resp.addHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, "
                + "If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, "
                + "Content-Type, X-E4M-With");
        resp.addHeader("Access-Control-Allow-Methods", "GET, POST");
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        chain.doFilter(request, response);

    }

    public void init(FilterConfig config) throws ServletException {
        System.out.println("过滤器初始化");
    }
}
