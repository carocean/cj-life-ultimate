package cj.life.ability.oauth2.gateway;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import cj.life.ability.oauth2.common.QueryStringUtils;
import cj.life.ability.oauth2.gateway.properties.AuthWebInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.util.Map;

public class DefaultAccessDeniedHandler implements ServerAccessDeniedHandler {
    String authWebLoginUrl;

    public DefaultAccessDeniedHandler(AuthWebInfo authWebInfo) {
        authWebLoginUrl = String.format("%s%s", authWebInfo.getHost(), authWebInfo.getLogin_url());
    }

    @SneakyThrows
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, AccessDeniedException e) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        ServerHttpRequest request = serverWebExchange.getRequest();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> params = request.getQueryParams().toSingleValueMap();
        String url = null;
        if (params.isEmpty()) {
            url = authWebLoginUrl;
        } else {
            url = String.format("%s?%s", authWebLoginUrl, QueryStringUtils.queryString(params));
        }
        ResultCode rc = ResultCode.ACCESS_DENIED;
        Object r = R.of(rc, url);
        return response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(new ObjectMapper().writeValueAsString(r).getBytes("UTF-8")))));
    }
}

