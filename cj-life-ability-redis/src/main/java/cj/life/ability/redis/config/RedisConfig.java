package cj.life.ability.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@ComponentScan(basePackages = {"cj.life.ability.redis"})
public class RedisConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

}