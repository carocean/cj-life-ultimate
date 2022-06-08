package cj.life.ability.oauth2.grant;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.web.DefaultSecurityFilterChain;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

public interface IGrantTypeAuthenticationFactory {

    AbstractAuthenticationToken extractAuthenticationToken(HttpServletRequest request);

    List<SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>> getSecurityConfigs();

    List<TokenGranter> getTokenGranters(AuthenticationManager authenticationManager, AuthorizationServerEndpointsConfigurer endpoints);

}
