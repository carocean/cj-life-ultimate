package cj.life.ability.oauth2.grant.sys;

import cj.life.ability.oauth2.grant.IGrantTypeCombin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class SysGrantTypeCombin implements IGrantTypeCombin {
    @Autowired
    SysSecurityConfig securityConfig;

    @Override
    public String getGrantType() {
        return null;
    }


    @Override
    public AbstractAuthenticationToken tryGetAuthenticationToken(HttpServletRequest request) {
        return null;
    }

    @Override
    public SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> getSecurityConfig() {
        return securityConfig;
    }

    @Override
    public TokenGranter getTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerEndpointsConfigurer endpoints) {
        return null;
    }
}
