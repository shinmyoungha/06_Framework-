package edu.kh.todo.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.kh.todo.common.interceptor.LoginMemberNPEInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginMemberNPEInterceptor loginMemberNPEInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginMemberNPEInterceptor)
                .addPathPatterns("/**/update","/**/insert","/**/editProfile","/**/changePw","/**/secession") // 로그인 필요 URL 패턴
                .excludePathPatterns("/", "/login", "/css/**", "/js/**", "/images/**"); // 예외 URL
    }
}
