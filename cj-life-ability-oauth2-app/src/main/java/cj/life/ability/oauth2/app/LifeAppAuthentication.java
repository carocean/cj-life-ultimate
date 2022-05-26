package cj.life.ability.oauth2.app;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class LifeAppAuthentication implements Authentication {
    LifeAppPrincipal principal;
    Collection<? extends GrantedAuthority> authorities;
    boolean isAuthenticated;
    LifeAppAuthenticationDetails details;

    public LifeAppAuthentication(LifeAppPrincipal principal, LifeAppAuthenticationDetails details, Collection<? extends GrantedAuthority> authorities) {
        this.principal = principal;
        this.authorities = authorities;
        this.details = details;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal == null ? "" : principal.user;
    }
}
