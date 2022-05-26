package cj.life.ability.oauth2.app.config;

import cj.life.ability.oauth2.app.LifeAppAuthentication;
import cj.life.ability.oauth2.app.LifeAppAuthenticationDetails;
import cj.life.ability.oauth2.app.LifeAppPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan({"cj.life.ability.oauth2.app"})
@ConditionalOnBean({AppSecurityWorkbin.class})
public class AppResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired(required = false)
    AuthenticationProvider authenticationProvider;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.stateless(false)
                .authenticationEntryPoint((request, response, authException) -> {
                    System.out.println("resources-----");
                }).tokenExtractor((request) -> {
                    String principal = request.getHeader("x-principal");
                    if (!StringUtils.hasText(principal)) {
                        throw new UsernameNotFoundException("x-principal");
                    }
                    String appid = request.getHeader("x-appid");
                    List<GrantedAuthority> authorityList = new ArrayList<>();
                    String roles = request.getHeader("x-roles");
                    if (StringUtils.hasText(roles)) {
                        String roleArr[] = roles.split(",");
                        for (String role : roleArr) {
                            authorityList.add(new SimpleGrantedAuthority(role));
                        }
                    }
                    LifeAppPrincipal lifeAppPrincipal = new LifeAppPrincipal(principal, appid, null);
                    LifeAppAuthenticationDetails details = new LifeAppAuthenticationDetails(request);
                    Authentication authentication = new LifeAppAuthentication(lifeAppPrincipal,details, authorityList);
                    return authentication;
                })
                .authenticationManager((authentication ->
                        authenticationProvider.authenticate(authentication)
                ))
        ;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().logout().disable().formLogin().disable()
                .authorizeRequests().antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling()
                .accessDeniedHandler(((request, response, accessDeniedException) -> {
                    System.out.println("accessDeniedHandler-----");
                }))
                .authenticationEntryPoint((request, response, authException) -> {
                    System.out.println("http-----");
                })
                .and()
                .authenticationProvider(authenticationProvider)
        ;
    }

}
