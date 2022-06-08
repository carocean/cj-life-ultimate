package cj.life.ability.oauth2.grant.sys;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

public class SysAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public SysAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userDetailsService, "userDetailsService is mandatory");
        Assert.notNull(this.passwordEncoder, "passwordEncoder is mandatory");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken adminLoginToken = (UsernamePasswordAuthenticationToken) authentication;
//        System.out.println("===进入Admin密码登录验证环节====="+ JSON.toJSONString(adminLoginToken));
        UserDetails userDetails = userDetailsService.loadUserByUsername(adminLoginToken.getName());
        //matches方法，前面为明文，后续为加密后密文
        //匹配密码。进行密码校验
        if (passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        }
        throw new BadCredentialsException("用户名密码不正确");
    }

}
