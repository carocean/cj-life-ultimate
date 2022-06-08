package cj.life.ability.oauth2.grant;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class GrantTypeAuthenticationFactory implements IGrantTypeAuthenticationFactory {
    Map<String, IGrantTypeCombin> combins;//key:grant_type

    public GrantTypeAuthenticationFactory(Map<String,IGrantTypeCombin> combins) {
        this.combins = new HashMap<>();
        for (IGrantTypeCombin combin : combins.values()) {
            this.combins.put(combin.getGrantType(), combin);
        }
    }

    @Override
    public AbstractAuthenticationToken extractAuthenticationToken(HttpServletRequest request) {
        String grant_type = request.getParameter("grant_type");
        if (StringUtils.hasText(grant_type) && combins.containsKey(grant_type)) {
            IGrantTypeCombin combin = combins.get(grant_type);
            return combin.tryGetAuthenticationToken(request);
        }
        for (IGrantTypeCombin combin : combins.values()) {
            AbstractAuthenticationToken requestAuth = combin.tryGetAuthenticationToken(request);
            if (request != null) {
                return requestAuth;
            }
        }
        return null;
    }

    @Override
    public List<SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>> getSecurityConfigs() {
        List<SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>> list = new ArrayList<>();
        for (IGrantTypeCombin combin : combins.values()) {
            list.add(combin.getSecurityConfig());
        }
        return list;
    }

    @Override
    public List<TokenGranter> getTokenGranters(AuthenticationManager authenticationManager, AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> list = new ArrayList<>();
        for (IGrantTypeCombin combin : combins.values()) {
            list.add(combin.getTokenGranter(authenticationManager,endpoints));
        }
        return list;
    }
}
