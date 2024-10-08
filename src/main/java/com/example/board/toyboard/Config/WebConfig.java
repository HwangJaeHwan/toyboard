package com.example.board.toyboard.Config;

import com.example.board.toyboard.interceptor.CheckUrlInterceptor;
import com.example.board.toyboard.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new CheckUrlInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/summernote/**", "/error","/jquery/**","/**/*.png");


        registry.addInterceptor(new LoginInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/summernote/**", "/jquery/**", "/error", "/login", "/logout", "/register", "/post","/","/**/*.png","/home");
    }
}
