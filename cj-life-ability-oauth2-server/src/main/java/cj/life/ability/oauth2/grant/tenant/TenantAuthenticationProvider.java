package cj.life.ability.oauth2.grant.tenant;

import cj.life.ability.oauth2.redis.ITenantStore;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import sun.security.provider.MD5;

import java.util.Map;

public class TenantAuthenticationProvider implements AuthenticationProvider {
    TokenStore tokenStore;

    ITenantStore tenantStore;
    public TenantAuthenticationProvider(TokenStore tokenStore,ITenantStore tenantStore) {
        this.tokenStore = tokenStore;
        this.tenantStore=tenantStore;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TenantAuthenticationToken tenantAuthenticationToken = (TenantAuthenticationToken) authentication;
//        System.out.println("===进入Admin密码登录验证环节====="+ JSON.toJSONString(adminLoginToken));
        //matches方法，前面为明文，后续为加密后密文
        //匹配密码。进行密码校验
        String access_token= (String) tenantAuthenticationToken.getCredentials();
        OAuth2Authentication storedAuth = tokenStore.readAuthentication(access_token);
        if (storedAuth == null) {
            throw new AuthenticationCredentialsNotFoundException(access_token);
        }
        //设置tenantid
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken((String) tenantAuthenticationToken.getCredentials());
        if (oAuth2AccessToken == null) {
            throw new InvalidTokenException(access_token);
        }
//        tokenStore.storeAccessToken(oAuth2AccessToken, exists);
        String client_id = "";
        if (storedAuth.getUserAuthentication() != null) {
            client_id = (String) ((Map<String, Object>) storedAuth.getUserAuthentication().getDetails()).get("client_id");
        }
//        String tenantKey = DigestUtils.md5Hex(String.format("%s%s",storedAuth.getName(),client_id));
        tenantStore.storeTenant(storedAuth.getName(),client_id,(String)tenantAuthenticationToken.getPrincipal());
        //虽然在以上过程只是重新读了一遍，检查这个用户登录过之后，
        // 令牌机制后续会为本次调用生成新令牌，但由于是同一用户，因此不会产生新令牌，只是把原OAuth2Authentication的details用本次的覆盖掉
        // 但同时系统会把tenantid存入到OAuth2Authentication.getUserAuthentication().getDetails()
        //由于返回的仍然是旧的exists对象，因此认证成功后生成的access_token应该等于上面传入的access_token
        return storedAuth;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return TenantAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
