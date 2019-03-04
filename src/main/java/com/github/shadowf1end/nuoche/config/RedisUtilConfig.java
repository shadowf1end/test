package com.github.shadowf1end.nuoche.config;

import com.github.shadowf1end.nuoche.common.util.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author su
 */
@Configuration
public class RedisUtilConfig {

    @Bean
    public RedisUtil redisUtil(StringRedisTemplate stringRedisTemplate) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(stringRedisTemplate);
        return redisUtil;
    }
}
