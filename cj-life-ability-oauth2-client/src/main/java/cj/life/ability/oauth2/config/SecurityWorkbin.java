package cj.life.ability.oauth2.config;

import cj.life.ability.oauth2.DefaultAccessDeniedHandler;
import cj.life.ability.oauth2.DefaultAuthenticationEntryPoint;
import cj.life.ability.oauth2.DefaultLogoutSuccessHandler;
import cj.life.ability.oauth2.DefaultUnauthorizedEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public abstract class SecurityWorkbin {

    @Bean
    public RestTemplate restTemplate()
    {
        RestTemplate restTemplate= new RestTemplate();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode() != 401){
                    super.handleError(response);
                }
            }
        });
        return restTemplate;
    }
    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return new DefaultUnauthorizedEntryPoint();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new DefaultAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new DefaultAuthenticationEntryPoint();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new DefaultLogoutSuccessHandler();
    }

    @Bean
    public List<SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>> defaultCodeSecurityConfigs() {
        return Arrays.asList(
        );
    }
}
