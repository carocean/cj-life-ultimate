package cj.life.ability.oauth2.gateway.annotation;

import cj.life.ability.oauth2.gateway.config.ResourceServerConfig;
import cj.life.ability.oauth2.gateway.web.rest.DefaultTokenResource;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ResourceServerConfig.class, DefaultTokenResource.class})
//@ConditionalOnWebApplication
public @interface EnableCjOAuth2Gateway {
}
