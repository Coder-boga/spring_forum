package com.example.forum.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppInterceptorConfigurer implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加登录拦截器
        registry.addInterceptor(loginInterceptor)       // 添加⽤⼾登录拦截器
                .addPathPatterns("/**")                 // 拦截所有请求
                .excludePathPatterns("/sign-in.html")   // 排除登录 HTML
                .excludePathPatterns("/sign-up.html")   // 排除注册 HTML
                .excludePathPatterns("/user/login")     // 排除登录 api 接⼝
                .excludePathPatterns("/user/register")  // 排除注册 api 接⼝
                .excludePathPatterns("/user/logout")    // 排除退出 api 接⼝
                .excludePathPatterns("/swagger*/**")    // 排除登录 swagger 下所有
                .excludePathPatterns("/v3*/**")         // 排除登录 v3 下所有，与swag
                .excludePathPatterns("/dist/**")        // 排除所有静态⽂件
                .excludePathPatterns("/image/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/**.ico");
    }
}
