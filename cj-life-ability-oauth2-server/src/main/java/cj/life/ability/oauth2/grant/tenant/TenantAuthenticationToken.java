package cj.life.ability.oauth2.grant.tenant;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class TenantAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public TenantAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public TenantAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
