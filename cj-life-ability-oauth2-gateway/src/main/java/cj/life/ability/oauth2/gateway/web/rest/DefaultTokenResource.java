package cj.life.ability.oauth2.gateway.web.rest;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import cj.life.ability.oauth2.common.QueryStringUtils;
import cj.life.ability.oauth2.gateway.ILookupClient;
import cj.life.ability.oauth2.gateway.properties.AuthServerInfo;
import cj.life.ability.oauth2.gateway.properties.ClientInfo;
import cj.life.ability.oauth2.gateway.properties.SecurityProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 该类用于客户端web请求令牌，该类作为中介向认证服务器请求，不能删除这些方法
 * 获取token一定要client密钥
 */
@RestController
public class DefaultTokenResource {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SecurityProperties securityProperties;

    @Autowired
    ILookupClient lookupClient;
    /*
    提供给客户端获取token，对于系统的授权类型，如授权码模式，需要跟根已获得的授权码向/oauth/token获取令牌。
    对于系统的简化模式，通过认证入口已直接向客户端通过重定向地址反回了访问令牌，但不含刷新令牌。但简化模式是不充许向/oauth/token请求令牌的
    对于自定义的授权类型：不能走认证流程，唯有向/oauth/token请求令牌。
     */
    @GetMapping("/token")
    public Mono<Map<String, Object>> token(ServerWebExchange exchange) throws IOException {
        try {
            return doToken(exchange);
        } catch (Exception e) {
            ResultCode rc = ResultCode.REQ_TOKEN_FAILURE;
            Object r = R.of(rc, e.getMessage());
            Map<String, Object> responseData = new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(r), HashMap.class);
            return Mono.just(responseData);
        }
    }

    private Mono<Map<String, Object>> doToken(ServerWebExchange exchange) throws JsonProcessingException {
        AuthServerInfo authServerInfo = securityProperties.getAuth_server();
        String url = String.format("%s%s", authServerInfo.getHost(), authServerInfo.getToken_url());
        ServerHttpRequest httpRequest = exchange.getRequest();
        Map<String, String> params = httpRequest.getQueryParams().toSingleValueMap();
        String client_id = params.getOrDefault("client_id", "");
        if (!StringUtils.hasText(client_id)) {
            ResultCode rc=ResultCode.INVALID_CLIENT;
            R r = R.of(rc,"require client_id in parameters.");
            Map<String, Object> responseData = new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(r), HashMap.class);
            return Mono.just(responseData);
        }
        String client_secret = params.getOrDefault("client_secret", "");
        if (!StringUtils.hasText(client_secret)) {
            ClientInfo client=lookupClient.lookup(client_id);
            if (client == null) {
                ResultCode rc=ResultCode.INVALID_CLIENT;
                R r = R.of(rc,"not found client "+client_id);
                Map<String, Object> responseData = new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(r), HashMap.class);
                return Mono.just(responseData);
            }
            client_secret=client.getClient_secret();
            params.put("client_secret", client_secret);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        String queryString = QueryStringUtils.queryString(params);
        HttpEntity<HashMap<String, Object>> request = new HttpEntity(queryString, headers);
        String json = restTemplate.postForObject(url, request, String.class);
        Map<String, Object> responseData = new ObjectMapper().readValue(json, HashMap.class);
        if (!params.containsKey("redirect_uri")||!isCustomGrantType(params)) {
            return Mono.just(responseData);
        }
        ServerHttpResponse response = exchange.getResponse();
        Map<String, Object> map = (Map<String, Object>) responseData.get("data");
        String redirect_uri = params.get("redirect_uri");
        String rewardUrl = String.format("%s?%s",
                redirect_uri, QueryStringUtils.queryString(map));
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().set("Location", rewardUrl);
        return Mono.empty();
    }

    private boolean isCustomGrantType(Map<String, String> params) {
        String responseTYpe = params.getOrDefault("response_type", "");
        if ("code".equals(responseTYpe) || "token".equals(responseTYpe)) {
            return false;
        }
        String grantType = params.getOrDefault("grant_type", "");
        AuthorizationGrantType gt= new AuthorizationGrantType(grantType);
        if (
                AuthorizationGrantType.AUTHORIZATION_CODE.equals(gt)
                        || AuthorizationGrantType.IMPLICIT.equals(gt)
                        || AuthorizationGrantType.CLIENT_CREDENTIALS.equals(gt)
                        || AuthorizationGrantType.PASSWORD.equals(gt)
                        || AuthorizationGrantType.REFRESH_TOKEN.equals(gt)) {
            return false;
        }
        return true;
    }


}
