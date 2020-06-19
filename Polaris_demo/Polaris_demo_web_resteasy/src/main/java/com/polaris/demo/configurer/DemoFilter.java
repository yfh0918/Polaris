package com.polaris.demo.configurer;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@WebFilter(filterName="demofilter",urlPatterns={"/*"}, initParams={
        @WebInitParam(name = "noLoginPaths", value = "index.jsp;fail.jsp;/LoginServlet")
        })
@Order(2)
public class DemoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("DemoFilter");
        System.out.println(request.getServletContext().getInitParameter("testdemo"));
        chain.doFilter(request, response);
    }

}
