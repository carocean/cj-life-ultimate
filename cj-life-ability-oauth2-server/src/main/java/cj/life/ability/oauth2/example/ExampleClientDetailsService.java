package cj.life.ability.oauth2.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class ExampleClientDetailsService implements ClientDetailsService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public ClientDetails loadClientByClientId(String client_id) throws ClientRegistrationException {
        if (!"client1".equals(client_id)) {
            throw new NoSuchClientException("不存在");
        }
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId(client_id);
        clientDetails.setClientSecret(passwordEncoder.encode("client1_secret"));
        //.resourceIds("res1")//资源列表
        //该客户端支持的认证类型："authorization_code", "password", "client_credentials", "implicit", "refresh_token"
        //如果使用authorization_code类型，则必须带上refresh_token类型，因为：令牌过期刷新需要用上refresh_token类型

        clientDetails.setAuthorizedGrantTypes(Arrays.asList("authorization_code", "refresh_token","implicit","sms_code","tenant_code"));
        clientDetails.setScope(Arrays.asList("all", "ROLE_ADMIN", "ROLE_USER"));
        clientDetails.setAutoApproveScopes(Arrays.asList("false"));//false需要用户确认授权才能跳到client_id
        Set<String> set = new HashSet<>();
        set.add("http://localhost:8084/home");
        clientDetails.setRegisteredRedirectUri(set);
        clientDetails.setAccessTokenValiditySeconds(7200);
        return clientDetails;
    }
}
