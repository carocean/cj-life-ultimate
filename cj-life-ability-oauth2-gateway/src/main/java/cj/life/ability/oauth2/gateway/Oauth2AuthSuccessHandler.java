package cj.life.ability.oauth2.gateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 认证成功处理类
 *
 * @author zlt
 * @date 2019/10/7
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
//y认证成功之后走这个
public class Oauth2AuthSuccessHandler implements ServerAuthenticationSuccessHandler {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        MultiValueMap<String, String> headerValues = new LinkedMultiValueMap<>(4);
        Object principal = authentication.getPrincipal();
        //客户端模式只返回一个clientId
//        if (principal instanceof SysUser) {
//            SysUser user = (SysUser)authentication.getPrincipal();
//            headerValues.add(SecurityConstants.USER_ID_HEADER, String.valueOf(user.getId()));
//            headerValues.add(SecurityConstants.USER_HEADER, user.getUsername());
//        }
        headerValues.add("x-principal",authentication.getPrincipal()+"");
        OAuth2Authentication oauth2Authentication = (OAuth2Authentication)authentication;
        String clientId = oauth2Authentication.getOAuth2Request().getClientId();
        headerValues.add("x-appid", clientId);
        String roles = "";
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            roles += String.format("%s,", authority.getAuthority());
        }
        if (roles.endsWith(",")) {
            roles = roles.substring(0, roles.length() - 1);
        }
        headerValues.add("x-roles",roles);
//        String accountType = AuthUtils.getAccountType(oauth2Authentication.getUserAuthentication());
//        if (StrUtil.isNotEmpty(accountType)) {
//            headerValues.add(SecurityConstants.ACCOUNT_TYPE_HEADER, accountType);
//        }
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                .headers(h -> h.addAll(headerValues))
                .build();

        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
        return webFilterExchange.getChain().filter(build);
    }
}
