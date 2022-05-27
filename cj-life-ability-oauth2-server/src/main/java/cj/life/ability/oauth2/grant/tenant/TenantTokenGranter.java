package cj.life.ability.oauth2.grant.tenant;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
逻辑是：
用户通过传入其access_token和tenantid进行认证（为什么不用refresh_token？因为简化模式不支持)
- 通过tokenStore，用access_token查得其Authentication对象，如果不存在则认证失败
- 然后为Authentication对象设置tenantid
- 最后用tokenStore存储该对象
- 用户收到新的access_token令牌，以此令牌即可访问网关
- 以上认证成功
 */
public class TenantTokenGranter extends AbstractTokenGranter {
    // 仅仅复制了 ResourceOwnerPasswordTokenGranter，只是改变了 GRANT_TYPE 的值，来验证自定义授权模式的可行性
    private static final String GRANT_TYPE = "tenant_code";

    private final AuthenticationManager authenticationManager;

    public TenantTokenGranter(
            AuthenticationManager authenticationManager,
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);

    }

    protected TenantTokenGranter(
            AuthenticationManager authenticationManager,
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }

    @Override

    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
        // 获取参数
        String access_token = parameters.get("access_token");
        if (!StringUtils.hasText(access_token)) {
            throw new InvalidGrantException("require parameter access_token");
        }
        String tenantid = parameters.get("tenantid");
        if (!StringUtils.hasText(tenantid)) {
            throw new InvalidGrantException("require parameter tenantid");
        }
        Authentication userAuth = new TenantAuthenticationToken(tenantid, access_token);
        OAuth2Authentication storedAuth = null;
        try {
            storedAuth = (OAuth2Authentication) authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        } catch (BadCredentialsException e) {
            // If the username/password are wrong the spec says we should send 400/invalid grant
            throw new InvalidGrantException(e.getMessage());
        }
        if (storedAuth == null || !storedAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate tenant: " + tenantid);
        }
//        Object authDetails = storedAuth.getUserAuthentication().getDetails();
        Object currDetails=storedAuth.getDetails();
        if (currDetails == null) {
            currDetails = new HashMap<>();
            storedAuth.setDetails(currDetails);
        }
        Map<String, Object> details = (Map<String, Object>) currDetails;
        details.put("tenantid", tenantid);
        return storedAuth;
    }
}

