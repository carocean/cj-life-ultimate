package cj.life.ability.oauth2.gateway;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.server.WebFilterExchange;
import reactor.core.publisher.Mono;

public interface IServerAuthenticationFailureHandler {
    Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, OAuth2Exception exception);
}
