package cj.life.ability.oauth2.app.config;

import cj.life.ability.oauth2.app.AppSecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

public class AppSecurityWorkbin {
    @Bean("customAppSecurityFilter")
    public OncePerRequestFilter appSecurityFilter() {
        return new AppSecurityFilter();
    }
}
