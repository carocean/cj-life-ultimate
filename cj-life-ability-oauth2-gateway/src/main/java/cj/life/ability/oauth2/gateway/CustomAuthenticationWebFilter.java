package cj.life.ability.oauth2.gateway;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class CustomAuthenticationWebFilter extends AuthenticationWebFilter {
    IServerAuthenticationFailureHandler serverAuthenticationFailureHandler;

    public CustomAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public CustomAuthenticationWebFilter(ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver) {
        super(authenticationManagerResolver);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return super.filter(exchange, chain).onErrorResume(OAuth2Exception.class, (ex) -> {
            return this.serverAuthenticationFailureHandler.onAuthenticationFailure(new WebFilterExchange(exchange, chain), ex);
        });
    }

    public void setServerAuthenticationFailureHandler(IServerAuthenticationFailureHandler serverAuthenticationFailureHandler) {
        this.serverAuthenticationFailureHandler = serverAuthenticationFailureHandler;
    }
}
