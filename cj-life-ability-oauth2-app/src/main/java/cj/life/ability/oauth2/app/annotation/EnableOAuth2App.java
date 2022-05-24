package cj.life.ability.oauth2.app.annotation;

import cj.life.ability.oauth2.app.config.AppSecurityConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({AppSecurityConfig.class})
//@ConditionalOnWebApplication
public @interface EnableOAuth2App {
}
