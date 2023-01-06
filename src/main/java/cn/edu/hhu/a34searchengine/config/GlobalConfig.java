package cn.edu.hhu.a34searchengine.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
public class GlobalConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                //表示允许跨域共享数据的请求类型
                .allowedMethods("POST","PUT","DELETE","GET")
                .allowCredentials(true)
                .maxAge(1800);
    }


}
