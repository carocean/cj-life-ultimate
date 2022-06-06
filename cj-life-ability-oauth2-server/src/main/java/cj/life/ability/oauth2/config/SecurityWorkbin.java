package cj.life.ability.oauth2.config;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import cj.life.ability.oauth2.*;
import cj.life.ability.oauth2.common.ResultCodeTranslator;
import cj.life.ability.oauth2.example.ExampleClientDetailsService;
import cj.life.ability.oauth2.example.ExampleUserDetailsService;
import cj.life.ability.oauth2.filter.LifeAuthenticationFilter;
import cj.life.ability.oauth2.grant.mobile.SmsCodeSecurityConfig;
import cj.life.ability.oauth2.grant.mobile.SmsCodeTokenGranter;
import cj.life.ability.oauth2.grant.sys.SysSecurityConfig;
import cj.life.ability.oauth2.grant.tenant.TenantSecurityConfig;
import cj.life.ability.oauth2.grant.tenant.TenantTokenGranter;
import cj.life.ability.oauth2.properties.SecurityProperties;
import cj.life.ability.oauth2.redis.RedisAuthCodeStoreServices;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.Arrays;
import java.util.List;

@EnableConfigurationProperties(SecurityProperties.class)
public abstract class SecurityWorkbin {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    SecurityProperties securityProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenStore tokenStore() {
        JedisConnectionFactory jedisConnectionFactory = applicationContext.getBean(JedisConnectionFactory.class);
        return new RedisTokenStore(jedisConnectionFactory);
    }

    @Bean("customClientDetailsService")
    @Primary
    public ClientDetailsService clientDetailsService() {
        return new ExampleClientDetailsService();
    }

    @Bean("customUserDetailsService")
    @Primary
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new ExampleUserDetailsService(passwordEncoder);
    }

    @Bean("customAuthCodeStoreServices")
    public AuthorizationCodeServices authCodeStoreServices() {
        return new RedisAuthCodeStoreServices();
    }

    @Bean("customSuccessAuthentication")
    public AuthenticationSuccessHandler successAuthentication() {
        return new DefaultSuccessAuthentication();
    }

    @Bean("customFailureAuthentication")
    public AuthenticationFailureHandler failureAuthentication() {
        return new DefaultFailureAuthentication();
    }

    @Bean("customLogoutSuccessHandler")
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new DefaultLogoutSuccessHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return ((request, response, e) -> {
            response.setStatus(HttpStatus.OK.value());
            response.setHeader("Content-Type", "application/json;charset=utf-8");
            ResultCode rc = ResultCodeTranslator.translateException(e);
            Object obj = R.of(rc, e.getMessage());
            response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
        });
    }

    @Bean
    public DefaultUnauthorizedEntryPoint unauthorizedEntryPoint() {
        return new DefaultUnauthorizedEntryPoint(securityProperties.getAuth_web());
    }

    @Bean
    public WebResponseExceptionTranslator exceptionTranslator() {
        return (e -> {
            OAuth2Exception exception = (OAuth2Exception) e;
            String errorCode = exception.getOAuth2ErrorCode();
            ResultCode rc = ResultCodeTranslator.translateResultCode(errorCode);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Content-Type", "application/json;charset=utf-8")
                    .body(R.of(rc, e.getMessage()));
        });
    }

    @Bean
    public List<SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>> defaultCodeSecurityConfigs() {
        SmsCodeSecurityConfig smsCodeSecurityConfig = applicationContext.getBean(SmsCodeSecurityConfig.class);
        SysSecurityConfig sysSecurityConfig = applicationContext.getBean(SysSecurityConfig.class);
        TenantSecurityConfig tenantSecurityConfig = applicationContext.getBean(TenantSecurityConfig.class);
        return Arrays.asList(
                smsCodeSecurityConfig,
                sysSecurityConfig,
                tenantSecurityConfig
        );
    }

    @Bean
    public List<AbstractTokenGranter> defaultTokenGranters(AuthenticationManager authenticationManager, AuthorizationServerEndpointsConfigurer endpoints) {
        return Arrays.asList(
                new SmsCodeTokenGranter(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()),
                new TenantTokenGranter(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory())
        );
    }

    @Bean
    public AbstractAuthenticationProcessingFilter defaultAuthenticationProcessingFilter(AuthenticationManager authenticationManager) {
        LifeAuthenticationFilter filter = new LifeAuthenticationFilter(authenticationManager);
        return filter;
    }
}
