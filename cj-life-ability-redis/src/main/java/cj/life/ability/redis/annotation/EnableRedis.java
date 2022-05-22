package cj.life.ability.redis.annotation;

import cj.life.ability.redis.config.RedisConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({RedisConfig.class})
//@ConditionalOnWebApplication
public @interface EnableRedis {
}
