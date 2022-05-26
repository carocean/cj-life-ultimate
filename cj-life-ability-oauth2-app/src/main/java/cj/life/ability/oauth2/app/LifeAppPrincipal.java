package cj.life.ability.oauth2.app;

import org.springframework.util.StringUtils;

import java.security.Principal;

public class LifeAppPrincipal implements Principal {
    String user;
    String appid;
    String tenantid;

    public LifeAppPrincipal() {
    }

    public LifeAppPrincipal(String user, String appid, String tenantid) {
        this.user = user;
        this.appid = appid;
        this.tenantid = tenantid;
    }

    @Override
    public String getName() {
        return user;
    }

    public String getAppid() {
        return appid;
    }

    public String getTenantid() {
        return tenantid;
    }

    @Override
    public String toString() {
        String fullName = (StringUtils.hasText(tenantid) ? tenantid + "::" : "")
                + (StringUtils.hasText(appid) ? appid + "::" : "")
                + (StringUtils.hasText(user) ? user : "");
        return fullName;
    }

    @Override
    public boolean equals(Object obj) {
        LifeAppPrincipal other = (LifeAppPrincipal) obj;
        if (other == null) {
            other = new LifeAppPrincipal();
        }
        return this.toString().equals(other.toString());
    }
}
