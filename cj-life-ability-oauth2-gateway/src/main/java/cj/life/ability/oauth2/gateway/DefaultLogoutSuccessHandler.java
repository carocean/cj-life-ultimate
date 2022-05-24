package cj.life.ability.oauth2.gateway;

import cj.life.ability.oauth2.common.QueryStringUtils;
import cj.life.ability.oauth2.gateway.properties.AuthServerInfo;
import cj.life.ability.oauth2.gateway.properties.AuthWebInfo;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.ServletRequestPathUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.util.Map;

public class DefaultLogoutSuccessHandler implements ServerLogoutSuccessHandler {
    @Autowired
    RestTemplate restTemplate;
    String authServerLogoutUrl;

    public DefaultLogoutSuccessHandler(AuthServerInfo authServerInfo) {
        authServerLogoutUrl = String.format("%s%s", authServerInfo.getHost(), authServerInfo.getLogout_url());
    }

    @SneakyThrows
    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        ServerHttpRequest request = exchange.getExchange().getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        Map<String, String> params = request.getQueryParams().toSingleValueMap();
        String url = null;
        if (params.isEmpty()) {
            url = authServerLogoutUrl;
        } else {
            url = String.format("%s?%s", authServerLogoutUrl, QueryStringUtils.queryString(params));
        }
        String json = restTemplate.getForObject(url, String.class);

        ServerHttpResponse response = exchange.getExchange().getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(json).getBytes("UTF-8")))));
    }

}
