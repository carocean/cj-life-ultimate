package cj.life.ability.oauth2.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.UUID;

@Configuration
@ComponentScan({"cj.life.ability.oauth2.app"})
public class AppSecurityConfig {
    @Autowired(required = false)
    @Qualifier("customAppSecurityFilter")
    OncePerRequestFilter appSecurityFilter;

    @Bean
    public FilterRegistrationBean companyUrlFilterRegister() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        //注入过滤器
        registration.setFilter(appSecurityFilter);
        //拦截规则
        registration.addUrlPatterns("/*");
        //过滤器名称
        registration.setName("life-" + UUID.randomUUID().toString());
        //过滤器顺序
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        return registration;
    }
}
