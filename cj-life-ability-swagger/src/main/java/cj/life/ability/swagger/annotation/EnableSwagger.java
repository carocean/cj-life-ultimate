package cj.life.ability.swagger.annotation;

import cj.life.ability.swagger.config.Swagger3Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({Swagger3Config.class})
//@ConditionalOnWebApplication
public @interface EnableSwagger {
}
