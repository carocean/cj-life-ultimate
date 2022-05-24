package cj.life.ability.oauth2.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("life.security")
@Setter
@Getter
public class SecurityProperties {
    AuthWebInfo auth_web;
    List<String> whitelist;
    List<String> static_resources;

    public SecurityProperties() {
        whitelist = new ArrayList<>();
        static_resources = new ArrayList<>();
        auth_web = new AuthWebInfo();
    }
}
