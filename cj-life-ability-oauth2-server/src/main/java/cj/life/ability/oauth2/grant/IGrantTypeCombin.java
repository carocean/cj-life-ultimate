package cj.life.ability.oauth2.grant;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.web.DefaultSecurityFilterChain;

import javax.servlet.http.HttpServletRequest;

public interface IGrantTypeCombin {
    String getGrantType();


    AbstractAuthenticationToken tryGetAuthenticationToken(HttpServletRequest request);

    SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> getSecurityConfig();

    TokenGranter getTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerEndpointsConfigurer endpoints);

}
