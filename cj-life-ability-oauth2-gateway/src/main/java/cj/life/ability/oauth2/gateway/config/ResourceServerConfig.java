package cj.life.ability.oauth2.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
@ComponentScan("cj.life.ability.oauth2.gateway")
@ConditionalOnBean({SecurityWorkbin.class})
public class ResourceServerConfig {

    @Autowired(required = false)
    SecurityWorkbin securityWorkbin;
    @Autowired(required = false)
    @Qualifier("customAuthenticationWebFilter")
    AuthenticationWebFilter authenticationWebFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        http.cors().and().csrf().disable()
                .logout().logoutUrl("/logout").logoutSuccessHandler(securityWorkbin.serverLogoutSuccessHandler())
                .and()
                .exceptionHandling()
                .accessDeniedHandler(securityWorkbin.accessDeniedHandler())
                .authenticationEntryPoint(securityWorkbin.authenticationEntryPoint())
                .and().authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll() //o
                .pathMatchers("/token", "/token/**", "/refresh_token", "/oauth2/**", "/logout").permitAll()  //无需进行权限过滤的请求路径
                .pathMatchers("/**").access(securityWorkbin.authManagerHandler())//访问权限拦截和实现处
                .anyExchange().authenticated()
        ;
        return http.build();
    }


}

