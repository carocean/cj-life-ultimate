package cj.life.ability.oauth2.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class LifeAppAuthenticationDetails {
    private String remoteAddress;
    private String sessionId;

    public LifeAppAuthenticationDetails(HttpServletRequest request) {
        this.remoteAddress = request.getRemoteAddr();
        HttpSession session = request.getSession(false);
        this.sessionId = session != null ? session.getId() : null;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }
}
