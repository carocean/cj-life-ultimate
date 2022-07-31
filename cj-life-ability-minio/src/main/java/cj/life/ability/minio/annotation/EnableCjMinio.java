package cj.life.ability.minio.annotation;

import cj.life.ability.minio.config.MinIoClientConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MinIoClientConfig.class})
//@ConditionalOnWebApplication
public @interface EnableCjMinio {
}
