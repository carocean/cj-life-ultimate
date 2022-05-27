package cj.life.ability.oauth2.grant.tenant;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

public class TenantAuthenticationProvider implements AuthenticationProvider {
    TokenStore tokenStore;

    public TenantAuthenticationProvider(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TenantAuthenticationToken tenantAuthenticationToken = (TenantAuthenticationToken) authentication;
//        System.out.println("===进入Admin密码登录验证环节====="+ JSON.toJSONString(adminLoginToken));
        //matches方法，前面为明文，后续为加密后密文
        //匹配密码。进行密码校验
        String access_token= (String) tenantAuthenticationToken.getCredentials();
        OAuth2Authentication exists = tokenStore.readAuthentication(access_token);
        if (exists == null) {
            throw new AuthenticationCredentialsNotFoundException(access_token);
        }
        //设置tenantid
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken((String) tenantAuthenticationToken.getCredentials());
        tokenStore.storeAccessToken(oAuth2AccessToken, exists);
        //由于返回的仍然是旧的exists对象，因此认证成功后生成的access_token应该等于上面传入的access_token
        return exists;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return TenantAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
