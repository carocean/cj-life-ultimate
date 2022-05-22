package cj.life.ability.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {"cj.life.ability.oauth2"})
@ConditionalOnBean({SecurityWorkbin.class})
public class WebSecurityConfig extends ResourceServerConfigurerAdapter {
    @Autowired(required = false)
    SecurityWorkbin securityWorkbin;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                //.tokenStore(tokenStore)//本地令牌校验
                .stateless(true)//关闭session
                .authenticationEntryPoint(securityWorkbin.authenticationEntryPoint())//authenticationEntryPoint  认证异常流程处理返回
        ;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .logout().logoutSuccessHandler(securityWorkbin.logoutSuccessHandler()).clearAuthentication(true).permitAll()
                .and()
                .authorizeRequests()
                //将自定义的/token和 refresh_token两个地址开放
                .antMatchers("/token", "/token/**", "/refresh_token", "/oauth2/**", "/logout").permitAll()
                .anyRequest()
//                .access("@rbacService.hasPermission(request,authentication)")//rbacService是自定义的鉴权
                .authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(securityWorkbin.accessDeniedHandler())
                .authenticationEntryPoint(securityWorkbin.unauthorizedEntryPoint())
        ;
        for (SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> c : securityWorkbin.defaultCodeSecurityConfigs()) {
            http.apply(c);
        }
    }

}
