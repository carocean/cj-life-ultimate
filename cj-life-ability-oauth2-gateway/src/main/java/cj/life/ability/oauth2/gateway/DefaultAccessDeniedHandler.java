package cj.life.ability.oauth2.gateway;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

public class DefaultAccessDeniedHandler implements ServerAccessDeniedHandler {
    @SneakyThrows
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, AccessDeniedException e) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String url = String.format("http://localhost:8083/login?response_type=code&client_id=client1&redirect_uri=http://localhost:8084%s&scope=all", "/home");
        ResultCode rc = ResultCode.ACCESS_DENIED;
        Object r = R.of(rc, url);
        return response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(new ObjectMapper().writeValueAsString(r).getBytes("UTF-8")))));
    }
}

