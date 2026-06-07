package com.dogrescue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
        // 显式注册 /images/** 静态资源处理器，优先级高于SPA转发
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // SPA路由转发：仅匹配前端路由路径，不拦截 /images、/api、/uploads 等
        // 根路径直接转发到 index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        String[] spaPaths = {"/home", "/dogs", "/dog", "/stories", "/story",
                "/donate", "/volunteer", "/lost-found", "/recognize", "/adopt-apply",
                "/admin", "/admin/login", "/admin/dashboard", "/admin/dogs",
                "/admin/adoptions", "/admin/donations", "/admin/stories",
                "/admin/volunteers", "/admin/lost-found", "/admin/users"};
        for (String path : spaPaths) {
            registry.addViewController(path).setViewName("forward:/index.html");
            registry.addViewController(path + "/**").setViewName("forward:/index.html");
        }
    }
}
