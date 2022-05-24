package cj.life.ability.oauth2.gateway.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthWebInfo {
    String host;
    String confirm_access_url;
    String login_url;
}
