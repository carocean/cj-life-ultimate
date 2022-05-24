package cj.life.ability.oauth2.gateway.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthServerInfo {
    String host;
    String logout_url;
    String token_url;
}
