package cj.life.ability.api.annotation;

import cj.life.ability.api.config.ApiWebAppConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ApiWebAppConfig.class})
//@ConditionalOnWebApplication
public @interface EnableApi {
}
