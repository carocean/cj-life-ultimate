package cj.life.ability.oauth2.config;

import cj.life.ability.oauth2.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.DefaultSecurityFilterChain;

import java.util.List;

/**
 * /oauth/authorize:      验证
 * /oauth/token:          获取token
 * /oauth/confirm_access: 用户授权
 * /oauth/error:          认证失败
 * /oauth/check_token:    资源服务器用来校验token
 * /oauth/token_key:      如果jwt模式则可以用此来从认证服务器获取公钥
 */
@EnableWebSecurity
@Configuration
@ComponentScan(basePackages = {"cj.life.ability.oauth2"})
@ConditionalOnBean({SecurityWorkbin.class})
@EnableConfigurationProperties(SecurityProperties.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    SecurityProperties securityProperties;
    @Autowired(required = false)
    SecurityWorkbin securityWorkbin;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //这里配置全局用户信息
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //"/assets/**", "/css/**", "/images/**"
        List<String> staticResources = securityProperties.getStatic_resources();
        web.ignoring().antMatchers(staticResources.toArray(new String[0]));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //"/login", "/oauth/**", "/logout"
        List<String>whitelist=securityProperties.getWhitelist();
        http.cors().and().csrf().disable().sessionManagement().disable()
                .exceptionHandling()
                .authenticationEntryPoint(securityWorkbin.unauthorizedEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers(whitelist.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().successHandler(securityWorkbin.successAuthentication()).failureHandler(securityWorkbin.failureAuthentication())
                .and()
                .logout().logoutSuccessHandler(securityWorkbin.logoutSuccessHandler()).clearAuthentication(true).permitAll()
        ;
        for (SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> c : securityWorkbin.defaultCodeSecurityConfigs()) {
            http.apply(c);
        }
        //这里引入扩展登陆的配置
//        http.apply(emailSecurityConfigurerAdapter)
//                .and().apply(mobileSecurityConfigurerAdapter)
//                .and().apply(socialSecurityConfigurerAdapter);
    }

}
