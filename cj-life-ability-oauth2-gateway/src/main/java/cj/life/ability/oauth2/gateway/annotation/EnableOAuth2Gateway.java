package cj.life.ability.oauth2.gateway.annotation;

import cj.life.ability.oauth2.gateway.config.ResourceServerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ResourceServerConfig.class})
//@ConditionalOnWebApplication
public @interface EnableOAuth2Gateway {
}
