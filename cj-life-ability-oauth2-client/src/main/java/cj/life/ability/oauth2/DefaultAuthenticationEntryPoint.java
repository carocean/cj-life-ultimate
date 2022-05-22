package cj.life.ability.oauth2;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class DefaultAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        //response_type=token 简化模式，等同于加上grant_type=implicit参数
        //response_type=code 授权码模式，等同于加上grant_type=authorization_code参数
//        String url = String.format("http://localhost:8083/login?response_type=token&client_id=client1&redirect_uri=http://localhost:8084%s&scope=all", "/home");
        String url = String.format("http://localhost:8083/login?response_type=code&client_id=client1&redirect_uri=http://localhost:8084%s&scope=all", "/home");
        ResultCode rc = ResultCode.IS_AUTHORIZED;
        Object obj = R.of(rc, url);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(obj));
    }
}
