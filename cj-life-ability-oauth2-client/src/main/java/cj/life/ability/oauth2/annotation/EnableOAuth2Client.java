package cj.life.ability.oauth2.annotation;

import cj.life.ability.oauth2.config.WebSecurityConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({WebSecurityConfig.class})
//@ConditionalOnWebApplication
public @interface EnableOAuth2Client {
}
