package com.example.Configs;

import com.example.Model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

@Configuration
public class RedisConfig {

    @Value("${redis.host.url}")
    String url;
    @Value("${redis.host.port}")
    Integer port;

    @Value("${redis.auth.password}")
    String password;



    @Bean
    public LettuceConnectionFactory getConnection()
    {
        RedisStandaloneConfiguration configuration=new RedisStandaloneConfiguration(url,port);
        configuration.setPassword(this.password);

        //Actual Connection , If you want to Use for Jedis there is Jedis Connection Factory similar to this.
        LettuceConnectionFactory connectionFactory=new LettuceConnectionFactory(configuration);
        return connectionFactory;
    }

    //Redis Template
    @Bean
    public RedisTemplate<String, Object> getTemplate()
    {
        RedisTemplate<String,Object> redisTemplate=new RedisTemplate<>();
        redisTemplate.setConnectionFactory(getConnection());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        return redisTemplate;
    }
}
