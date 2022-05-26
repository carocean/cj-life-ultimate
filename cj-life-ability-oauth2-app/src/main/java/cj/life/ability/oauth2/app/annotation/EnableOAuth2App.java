package cj.life.ability.oauth2.app.annotation;

import cj.life.ability.oauth2.app.config.AppResourceServerConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({AppResourceServerConfig.class})
//@ConditionalOnWebApplication
public @interface EnableOAuth2App {
}
