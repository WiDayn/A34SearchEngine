package cn.edu.hhu.a34searchengine.config;

import cn.edu.hhu.a34searchengine.interceptor.LoadInterceptor;
import cn.edu.hhu.a34searchengine.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    private final LoadInterceptor loadInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error")
                .excludePathPatterns("/onlineLoad/**");

        registry.addInterceptor(loadInterceptor)
                .addPathPatterns("/onlineLoad/**")
                .excludePathPatterns("/error");
    }
}
