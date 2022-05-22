package cj.life.ability.oauth2.gateway;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class DefaultSecurityGlobalFilter implements WebFilter {

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String token = request.getHeaders().getFirst("Authorization");
        if (!StringUtils.hasText(token)) {
            ResultCode rc = ResultCode.INVALID_TOKEN;
            R r = R.of(rc, "Authorization is Empty in Header.");
            DataBuffer buffer = response.bufferFactory().wrap(new ObjectMapper().writeValueAsString(r).getBytes());
            return response.writeWith(Mono.just(buffer));
        }
        // 解析JWT获取jti，以jti为key判断redis的黑名单列表是否存在，存在则拦截访问

        token = token.substring("Bearer ".length());
        if (!StringUtils.hasText(token)) {
            ResultCode rc = ResultCode.INVALID_TOKEN;
            R r = R.of(rc, "AccessToken is Null.");
            DataBuffer buffer = response.bufferFactory().wrap(new ObjectMapper().writeValueAsString(r).getBytes());
            return response.writeWith(Mono.just(buffer));
        }

        //给header里面添加值
        ServerHttpRequest tokenRequest = exchange.getRequest().mutate().header("access_token", token).build();
        ServerWebExchange build = exchange.mutate().request(tokenRequest).build();
        return chain.filter(build);
    }

}

