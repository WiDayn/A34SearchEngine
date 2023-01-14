package cn.edu.hhu.a34searchengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig
{
    @Bean
    public RedisTemplate<Long, Object> redisTemplate(RedisConnectionFactory factory){
        //实例化这个Bean
        RedisTemplate<Long, Object> template = new RedisTemplate<>();
        //把工厂设置给Template
        template.setConnectionFactory(factory);
        //配置Template主要配置序列化的方式，因为写的是java程序，得到的是java类型的数据，最终要这个数据存储到数据库里面
        //就要指定一种序列化的方式，或者说数据转换的方式
        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.java());
        //设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.java());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();//使上面参数生效
        return template;
    }
}