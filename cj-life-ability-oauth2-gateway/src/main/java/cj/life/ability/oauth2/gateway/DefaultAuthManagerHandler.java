package cj.life.ability.oauth2.gateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class DefaultAuthManagerHandler implements ReactiveAuthorizationManager<AuthorizationContext> {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();
//自定义地址权限拦截实现
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
        ServerHttpRequest request = object.getExchange().getRequest();
//        String requestUrl = request.getPath().pathWithinApplication().value();
        //这里可以根据requestUrl查询redis，或者数据库得到requestUrl所需的角色，放入roles中
//       List<String> roles = new ArrayList<>();
//        if (antPathMatcher.match("/resource/admin/**",requestUrl)){
//            roles.add("ROLE_admin");
//        }else {
//            roles.add("ROLE_common");
//        }
//        roles.add("ROLE_admin");
        return authentication
                .filter(a -> a.isAuthenticated())
                .flatMapIterable(a -> a.getAuthorities())
                .map(g -> g.getAuthority())
                .any(c -> {
//                    if (roles.contains(String.valueOf(c))) {
//                        return true;
//                    }
                    return true;
                })
                .map(hasAuthority -> new AuthorizationDecision(hasAuthority))
                .defaultIfEmpty(new AuthorizationDecision(true));
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return null;
    }
}


