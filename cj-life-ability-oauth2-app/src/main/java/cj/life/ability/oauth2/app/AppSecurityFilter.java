package cj.life.ability.oauth2.app;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppSecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext ctx = SecurityContextHolder.getContext();
        String principal = request.getHeader("x-principal");
        if (!StringUtils.hasText(principal)) {
            response.getWriter().write(new ObjectMapper().writeValueAsString(R.of(ResultCode.USERNAME_NOT_FOUND, "未经网关鉴权，因此缺少来访用户")));
            return;
        }
        if (ctx == null) {
            String appid = request.getHeader("x-appid");
            List<GrantedAuthority> authorityList = new ArrayList<>();
            String roles = request.getHeader("x-roles");
            if (StringUtils.hasText(roles)) {
                String roleArr[] = roles.split(",");
                for (String role : roleArr) {
                    authorityList.add(new SimpleGrantedAuthority(role));
                }
            }
            Authentication authentication = new AppAuthentication(principal,appid, authorityList, true);
            ctx = new SecurityContextImpl(authentication);
            SecurityContextHolder.setContext(ctx);
        }
        filterChain.doFilter(request, response);
    }
}
