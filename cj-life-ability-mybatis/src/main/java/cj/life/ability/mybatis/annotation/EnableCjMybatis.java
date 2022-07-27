package cj.life.ability.mybatis.annotation;

import cj.life.ability.mybatis.config.DataSourceConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({DataSourceConfig.class})
//@ConditionalOnWebApplication
public @interface EnableCjMybatis {
}
