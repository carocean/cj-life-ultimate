package cj.life.ability.oauth2;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class DefaultWebRequestInterceptor implements HandlerInterceptor {

    //自定义用户确认页到认证前端web，也可以拦截认证失败地址到认证前端web处理
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().startsWith("/oauth/confirm_access")) {
            Map<String, String[]> paramMap = request.getParameterMap();
            StringBuilder param = new StringBuilder();
            paramMap.forEach((k, v) -> {
                param.append("&").append(k).append("=").append(v[0]);
            });
            param.deleteCharAt(0);
            String authUrl = "http://localhost:8083/confirm_access?" + param.toString();
            response.sendRedirect(authUrl);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
