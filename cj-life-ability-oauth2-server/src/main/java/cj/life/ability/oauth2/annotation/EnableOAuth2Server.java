package cj.life.ability.oauth2.annotation;

import cj.life.ability.oauth2.config.AuthorizationServerConfig;
import cj.life.ability.oauth2.config.SecurityWorkbin;
import cj.life.ability.oauth2.config.WebSecurityConfig;
import cj.life.ability.redis.config.RedisConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({AuthorizationServerConfig.class, WebSecurityConfig.class})
//@ConditionalOnWebApplication
public @interface EnableOAuth2Server {
}
