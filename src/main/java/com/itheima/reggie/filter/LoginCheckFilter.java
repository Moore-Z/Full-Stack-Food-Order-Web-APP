package com.itheima.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        // 1. Get this time request URL
        String requestURI = request.getRequestURI();


        log.info("Find filtered URI: {}", requestURI);
        // 2. Define non-request pathes
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
                "/user/sendMsg"
        };

        // 3. Find Whether we need to process
        boolean find = check(urls,requestURI);
        if(find){
            log.info("find match Permited URI: {}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        // 4-1. find Whether we have login in already
        if(request.getSession().getAttribute("employee") !=null){
            log.info("Find Logined employee: {}",request.getSession().getAttribute("employee") );

            Long emp = (Long)request.getSession().getAttribute("employee");

            BaseContext.setCurrentId(emp);
            long id = Thread.currentThread().getId();
            log.info("Thread id: {}",id);

            filterChain.doFilter(request,response);
            return;
        }
        //4-2
        if(request.getSession().getAttribute("user") !=null){
            log.info("Find Logined employee: {}",request.getSession().getAttribute("user") );

            Long userid = (Long)request.getSession().getAttribute("user");

            BaseContext.setCurrentId(userid);
            long id = Thread.currentThread().getId();
            log.info("Thread id: {}",id);

            filterChain.doFilter(request,response);
            return;
        }

        //5. the URI is not on the list and we did not login in employee, return into login page
        log.info("No user logined");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;







    }

    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
