package cj.life.ability.oauth2.app.config;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import cj.life.ability.oauth2.app.LifeAppAuthentication;
import cj.life.ability.oauth2.app.LifeAppAuthenticationDetails;
import cj.life.ability.oauth2.app.LifeAppPrincipal;
import cj.life.ability.oauth2.common.ResultCodeTranslator;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan({"cj.life.ability.oauth2.app"})
@ConditionalOnBean({AppSecurityWorkbin.class})
@Slf4j
public class AppResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired(required = false)
    AuthenticationProvider authenticationProvider;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.stateless(false)
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ResultCode rc = ResultCodeTranslator.translateException(authException);
                    Object obj = R.of(rc, authException.getMessage());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
                }).tokenExtractor((request) -> {
                    String swaggerToken = request.getHeader("swagger_token");
                    if (!StringUtils.hasText(swaggerToken)) {
                        swaggerToken = request.getParameter("swagger_token");
                    }
                    if (StringUtils.hasText(swaggerToken)) {
                        return extractSwaggerToken(swaggerToken,request);
                    }
                    String user = request.getHeader("x-user");
                    if (!StringUtils.hasText(user)) {
                        return null;//默认是资源全部开放，包括对swagger资源
//                        throw new UsernameNotFoundException("x-user");
                    }
                    String appid = request.getHeader("x-appid");
                    List<GrantedAuthority> authorityList = new ArrayList<>();
                    String roles = request.getHeader("x-roles");
                    if (StringUtils.hasText(roles)) {
                        String roleArr[] = roles.split(",");
                        for (String role : roleArr) {
                            authorityList.add(new SimpleGrantedAuthority(role));
                        }
                    }
                    String tenantid = request.getHeader("x-tenantid");
                    LifeAppPrincipal lifeAppPrincipal = new LifeAppPrincipal(user, appid, tenantid);
                    LifeAppAuthenticationDetails details = new LifeAppAuthenticationDetails(request);
                    Authentication authentication = new LifeAppAuthentication(lifeAppPrincipal, details, authorityList);
                    return authentication;
                })
                .authenticationManager((authentication ->
                        authenticationProvider.authenticate(authentication)
                ))
        ;
    }

    private Authentication extractSwaggerToken(String swaggerToken, HttpServletRequest request) {
        //租户标识::应用标识::用户::角色1,角色2
        String[] terms = swaggerToken.split("::");
        if (terms.length != 4) {
            log.warn("swagger_token格式不正确，抽取令牌过程被中止，正确格式：租户标识::应用标识::用户::角色1,角色2，如果某项为空但::分隔不能少");
            return null;
        }
        String user = terms[2];
        if (!StringUtils.hasText(user)) {
            return null;//默认是资源全部开放，包括对swagger资源
//                        throw new UsernameNotFoundException("x-user");
        }
        String appid = terms[1];
        List<GrantedAuthority> authorityList = new ArrayList<>();
        String roles = terms[3];
        if (StringUtils.hasText(roles)) {
            String roleArr[] = roles.split(",");
            for (String role : roleArr) {
                authorityList.add(new SimpleGrantedAuthority(role));
            }
        }
        String tenantid = terms[0];
        LifeAppPrincipal lifeAppPrincipal = new LifeAppPrincipal(user, appid, tenantid);
        LifeAppAuthenticationDetails details = new LifeAppAuthenticationDetails(request);
        Authentication authentication = new LifeAppAuthentication(lifeAppPrincipal, details, authorityList);
        return authentication;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().logout().disable().formLogin().disable()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling()
                .accessDeniedHandler(((request, response, e) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ResultCode rc = ResultCode.ACCESS_DENIED;
                    Object r = R.of(rc, e.getMessage());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(r));
                }))
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ResultCode rc = ResultCodeTranslator.translateException(authException);
                    Object obj = R.of(rc, authException.getMessage());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
                })
                .and()
                .authenticationProvider(authenticationProvider)
        ;
    }

}
