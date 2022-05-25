package cj.life.ability.oauth2.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"cj.life.ability.oauth2.app"})
@ConditionalOnBean({AppSecurityWorkbin.class})
public class AppSecurityConfig {
    @Autowired(required = false)
    AppSecurityWorkbin appSecurityWorkbin;
    @Bean("lifeAppSecurityFilter")
    public FilterRegistrationBean securityFilterRegister() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        //注入过滤器
        registration.setFilter(appSecurityWorkbin.appSecurityFilter());
        //拦截规则
        registration.addUrlPatterns("/*");
        //过滤器名称
        registration.setName("lifeAppSecurityFilter");
        //过滤器顺序
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        return registration;
    }

}
