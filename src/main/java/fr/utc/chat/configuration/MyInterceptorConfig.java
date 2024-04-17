package fr.utc.chat.configuration;

import fr.utc.chat.intercepter.AdminInterceptor;
import fr.utc.chat.intercepter.SessionTokenInterceptor;
import fr.utc.chat.intercepter.ChatTokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MyInterceptorConfig implements WebMvcConfigurer {
    @Resource
    AdminInterceptor adminInterceptor;
    @Resource
    ChatTokenInterceptor chatTokenInterceptor;

    @Resource
    SessionTokenInterceptor sessionTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(chatTokenInterceptor).addPathPatterns("/chat/**").excludePathPatterns("/chat/user_login").excludePathPatterns("/chat/create_user");
        registry.addInterceptor(sessionTokenInterceptor)
                .addPathPatterns("/admin/**").order(1);
        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**").order(2);
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")//前端发送的请求中，允许域名为http://localhost:3000访问
                .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE")//前端发送的请求中,允许请求方式"GET","POST","PUT","OPTIONS","DELETE"访问
                .allowCredentials(true)
                .allowedHeaders("*","Content-Type")
                .maxAge(3600);
    }


}
