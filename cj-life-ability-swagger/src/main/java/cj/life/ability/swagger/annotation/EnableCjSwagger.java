package cj.life.ability.swagger.annotation;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@ComponentScan(basePackages = {"cj.life.ability.swagger"})
//@Import({Swagger3Config.class, FixNpeForSpringfoxHandlerProviderBeanPostProcessorConfiguration.class})
//@ConditionalOnWebApplication
public @interface EnableCjSwagger {
}
