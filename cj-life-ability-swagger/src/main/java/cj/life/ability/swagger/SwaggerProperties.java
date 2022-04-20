package cj.life.ability.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("life.swagger")
public class SwaggerProperties {
    String groupName;
    private boolean enabled = false;
    private String basePackage;
    private ApiInfoProperties apiInfo;
    private List<SwaggerResponseMsg> responseMsg;

    public List<SwaggerResponseMsg> getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(List<SwaggerResponseMsg> responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public ApiInfoProperties getApiInfo() {
        return apiInfo;
    }

    public void setApiInfo(ApiInfoProperties apiInfo) {
        this.apiInfo = apiInfo;
    }
}
