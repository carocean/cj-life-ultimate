package cj.life.ability.oauth2;

import cj.life.ability.api.R;
import cj.life.ability.api.ResultCode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        String response_type = request.getParameter("response_type");
        String client_id = request.getParameter("client_id");
        String grant_type = request.getParameter("grant_type");
        String redirect_uri = request.getParameter("redirect_uri");
        String scope = request.getParameter("scope");
        Map<String, String> map = new HashMap<>();
        if (StringUtils.hasText(response_type)) {
            map.put("response_type", response_type);
        }
        if (StringUtils.hasText(grant_type)) {
            map.put("grant_type", grant_type);
        }
        if (StringUtils.hasText(client_id)) {
            map.put("client_id", client_id);
        }
        if (StringUtils.hasText(redirect_uri)) {
            map.put("redirect_uri", redirect_uri);
        }
        if (StringUtils.hasText(scope)) {
            map.put("scope", scope);
        }

        Object obj = R.of(rc, map);
        response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
    }
}
