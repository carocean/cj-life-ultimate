package cj.life.ability.oauth2.grant.mobile;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SmsCodeAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public SmsCodeAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public SmsCodeAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
