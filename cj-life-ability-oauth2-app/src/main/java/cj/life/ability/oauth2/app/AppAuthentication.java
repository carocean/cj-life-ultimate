package cj.life.ability.oauth2.app;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class AppAuthentication implements Authentication {
    String principal;
    String appid;
    List<GrantedAuthority> grantedAuthorityList;
    boolean isAuthenticated;

    public AppAuthentication(String principal,String appid, List<GrantedAuthority> grantedAuthorityList, boolean isAuthenticated) {
        this.principal = principal;
        this.appid=appid;
        this.grantedAuthorityList = grantedAuthorityList;
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorityList;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
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

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    @Override
    public String getName() {
        return principal;
    }
}
