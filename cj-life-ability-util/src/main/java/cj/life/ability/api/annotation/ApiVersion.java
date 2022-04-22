package cj.life.ability.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API版本控制注解
 * <pre>
 *     例：/api/{version}/test/a，该地址表示方法a会有多个版本的实现，但系统无法告知方法a有几个版本。
 *          如果调用者指定的版本过高在系统中未有实现，则系统自动使用最大版本，即最新版本
 *
 *     使用：注解加到类型上表示所有方法的默认版本，如果加到方法上即为最终指定版本。
 *     建议：虽然同一类型下可定义同一请求的各个版本方法，但仍推荐不同版本放到不同类型版本中，
 *     这样比较清晰。即同一请求地址下，以不同类型实现各版本。各版本类型名后加数字表示。
 *
 *     注：- 该注解支持省略，如类型定义了包含{version}的变量地址：/api/{version}/test。虽然不用注解，但会默认版本1。
 *         - springboot启动时报信息：Generating unique operation named，
 *     只是提示，正常使用，忽略即可，或将springfox的日志提升为错误级关闭所有提示。
 *     出现原因是当两个类型声明相同地址时提示该信息，而同一类型有相同地址方法时没有提示。
 * </pre>
 * API版本控制注解<br>
 * 注：如果版本是默认版本1则可不使用该注解。<br>
 * 用法，如：<br>
 *
 * @RequestMapping("api/{version}/user") <br>
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    /**
     * @return 版本号
     */
    int value() default 1;
}

