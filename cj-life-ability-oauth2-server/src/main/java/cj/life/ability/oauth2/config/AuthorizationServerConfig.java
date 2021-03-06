package cj.life.ability.oauth2.config;

import cj.life.ability.oauth2.DefaultWebRequestInterceptor;
import cj.life.ability.oauth2.properties.SecurityProperties;
import cj.life.ability.oauth2.filter.DefaultClientCredentialsTokenEndpointFilter;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@EnableAuthorizationServer
@Configuration
@ComponentScan(basePackages = {"cj.life.ability.oauth2"})
@ConditionalOnBean({SecurityWorkbin.class})
@EnableConfigurationProperties(SecurityProperties.class)
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired(required = false)
    SecurityWorkbin securityWorkbin;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired(required = false)
    @Qualifier("customUserDetailsService")
    UserDetailsService userDetailsService;
    @Autowired
    SecurityProperties securityProperties;
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

        DefaultClientCredentialsTokenEndpointFilter endpointFilter = new DefaultClientCredentialsTokenEndpointFilter(security);
        endpointFilter.afterPropertiesSet();
        endpointFilter.setAuthenticationEntryPoint(securityWorkbin.authenticationEntryPoint());
        security.allowFormAuthenticationForClients()
                .checkTokenAccess("isAuthenticated()")
//                .tokenKeyAccess("isAuthenticated()")
                .tokenKeyAccess("permitAll()")
                .addTokenEndpointAuthenticationFilter(endpointFilter);
        ;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(securityWorkbin.clientDetailsService());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        HandlerInterceptor interceptor = new DefaultWebRequestInterceptor(securityProperties.getAuth_web());
        endpoints.addInterceptor(interceptor);
        endpoints.authenticationManager(authenticationManager);
        endpoints.userDetailsService(userDetailsService);
        endpoints.tokenStore(securityWorkbin.tokenStore());
        endpoints.tokenEnhancer(createTokenEnhancer());
        endpoints.tokenGranter(tokenGranter(endpoints));
        endpoints.authorizationCodeServices(securityWorkbin.authCodeStoreServices());
        endpoints.exceptionTranslator(securityWorkbin.exceptionTranslator());
//        refresh token????????????????????????????????????(true)??????????????????(false)????????????true
//        - ???????????????access token?????????????????? refresh token?????????????????????????????????????????????????????????
//        - ??????????????????access token?????????????????? refresh token????????????????????????refresh token???????????????????????????????????????
        endpoints.reuseRefreshTokens(false);
        super.configure(endpoints);
    }
//??????granters

    /*
     * ??????granter??????????????????????????????????????????????????????????????????????????????????????????????????????????????????authProvider???authDetailsServices.
     * ??????????????????????????????????????????(authEntrypoint)???????????????????????????/oauth/authorize??????????????????/oauth/token??????????????????????????????(tokenEntrypoint???
     * ???????????????5?????????????????????????????????????????????????????????????????????????????????(/oauth/token)
     * @param endpoints
     * @return
     */
    private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> granters = new ArrayList<>(Arrays.asList(endpoints.getTokenGranter()));// ???????????????granter??????
        granters.addAll(
                securityWorkbin.grantTypeAuthenticationFactory().getTokenGranters(authenticationManager,endpoints)
                );
        return new CompositeTokenGranter(granters);
    }

    private TokenEnhancer createTokenEnhancer() {
        return (accessToken, authentication) -> {
            if (accessToken instanceof DefaultOAuth2AccessToken) {
                DefaultOAuth2AccessToken token = ((DefaultOAuth2AccessToken) accessToken);
                token.setValue(createNewToken());
                token.setRefreshToken(new DefaultOAuth2RefreshToken(createNewToken()));
                token.setAdditionalInformation(accessToken.getAdditionalInformation());
                return token;
            }
            return accessToken;
        };
    }

    private String createNewToken() {
        return DigestUtils.md5Hex(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }
}
