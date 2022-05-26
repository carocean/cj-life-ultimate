package cj.life.ability.oauth2.app.config;

import cj.life.ability.oauth2.app.LifeAppAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class AppSecurityWorkbin {
    @Bean
    public AuthenticationProvider appAuthenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                authentication.setAuthenticated(true);
                return authentication;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return LifeAppAuthentication.class.isAssignableFrom(authentication);
            }
        };
    }
}
