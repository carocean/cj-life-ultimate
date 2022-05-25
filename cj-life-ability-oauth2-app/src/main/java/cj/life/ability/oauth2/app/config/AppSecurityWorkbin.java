package cj.life.ability.oauth2.app.config;

import cj.life.ability.oauth2.app.AppSecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class AppSecurityWorkbin {
    protected OncePerRequestFilter appSecurityFilter() {
        return new AppSecurityFilter();
    }
}
