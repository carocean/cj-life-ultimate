package cj.life.ability.oauth2;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: cj
 * @create: 2018-12-02 09:24
 * @description:
 */
@Component("successAuthentication")
public class DefaultSuccessAuthentication extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ResultCode rc = ResultCode.IS_AUTHORIZED;
        Object obj = R.of(rc, request.getQueryString());
        response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
    }
}
