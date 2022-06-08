package cj.life.ability.oauth2.grant.tenant;

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
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class TenantGrantTypeCombin implements IGrantTypeCombin {
    @Autowired
    TenantSecurityConfig tenantSecurityConfig;

    @Override
    public String getGrantType() {
        return TenantTokenGranter.GRANT_TYPE;
    }


    @Override
    public AbstractAuthenticationToken tryGetAuthenticationToken(HttpServletRequest request) {
        String access_token = request.getParameter("access_token");
        String tenantid = request.getParameter("tenantid");
        if (StringUtils.hasText(access_token) && StringUtils.hasText(tenantid)) {
            return new TenantAuthenticationToken(tenantid, access_token);
        }
        return null;
    }

    @Override
    public SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> getSecurityConfig() {
        return tenantSecurityConfig;
    }

    @Override
    public TokenGranter getTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerEndpointsConfigurer endpoints) {
        return new TenantTokenGranter(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory());
    }
}
