package cj.life.ability.oauth2.gateway;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

public class DefaultUnauthorizedEntryPoint implements ServerAuthenticationEntryPoint {

    @SneakyThrows
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //Authorization:Bearer
        //可以写到8080，由8080告知认证服务器的登录地址，这样应用只需要知道认证后端地址就可以了
        //如果grant_type=authorization_code 则可以省略此参数
        //response_type:code（授权码模式下springOAuth2规定为code）
        //response_type:token（是implicit 简化模式）
        //response_type参数是必须的，要么是code要么是token只有这两种，只有验证码模式是code,其它包括自定义都是token
        //response_type=token则直接返回access_token
        //其它模式用grant_type
        //扩展grant_type均不能访问/oauth/authorize,即不能使用授权码或简化模式的流程。扩展的授权类型只能直接向/oauth/token请求令牌
        //implicit 简化模式/oauth/token
        //扩展模式 grant_type：https://www.jianshu.com/p/0cb6741b9890
        //指定刷新模式直接返回access_token:localhost:8080/oauth/token?client_id=client_id_1&client_secret=123456&grant_type=refresh_token&refresh_token=fca82077-720e-46da-9c88-c2f221b0cb46
        //response_type=token 简化模式，等同于加上grant_type=implicit参数
        //response_type=code 授权码模式，等同于加上grant_type=authorization_code参数
//        String url = String.format("http://localhost:8083/login?response_type=token&client_id=client1&redirect_uri=http://localhost:8084%s&scope=all", "/home");
        //留给登录界面来选择授权类型及响应类型
        String url = String.format("http://localhost:8083/login?client_id=client1&redirect_uri=http://localhost:8084%s&scope=all", "/home");
        ResultCode rc = ResultCode.UNAUTHORIZED_CLIENT;
        Object r = R.of(rc, url);
        return response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(new ObjectMapper().writeValueAsString(r).getBytes("UTF-8")))));
    }
}

