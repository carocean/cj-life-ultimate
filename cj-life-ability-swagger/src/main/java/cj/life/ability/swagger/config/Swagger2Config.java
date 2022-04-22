package cj.life.ability.swagger.config;

import cj.life.ability.swagger.SwaggerProperties;
import cj.life.ability.swagger.SwaggerResponseMsg;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 使用knife4j替代swagger-ui
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@EnableConfigurationProperties(SwaggerProperties.class)
@Slf4j
public class Swagger2Config {
    @Autowired
    private SwaggerProperties swaggerProperties;

    @Bean
//    @ConditionalOnProperty(value = "life.swagger")
    @ConditionalOnMissingBean(Docket.class)
    public Docket createRestApi() {
        log.info(String.format("Swagger2开启状态：%s", swaggerProperties.isEnabled()));
        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2)
                .groupName(swaggerProperties.getGroupName())
                //加载配置信息
                .apiInfo(apiInfo())
                .enable(swaggerProperties.isEnabled())
                //设置全局参数
                .globalOperationParameters(globalParamBuilder())
                //设置全局响应参数
                .globalResponseMessage(RequestMethod.GET, responseBuilder())
                .globalResponseMessage(RequestMethod.POST, responseBuilder())
                .globalResponseMessage(RequestMethod.PUT, responseBuilder())
                .globalResponseMessage(RequestMethod.DELETE, responseBuilder())
                .select();

        //加载swagger 扫描包
        builder.apis(RequestHandlerSelectors.withClassAnnotation(Api.class));
        if (!StringUtils.hasText(swaggerProperties.getBasePackage())) {
            builder.apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()));
        }

        return builder.paths(PathSelectors.any()).build()
                //设置安全认证
                .securityContexts(securityContexts())
                .securitySchemes(securitySchemes());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getApiInfo().getTitle())
                .description(swaggerProperties.getApiInfo().getDescription())
                .contact(new Contact(swaggerProperties.getApiInfo().getContact(), swaggerProperties.getApiInfo().getUrl(), swaggerProperties.getApiInfo().getEmail()))
                .version(swaggerProperties.getApiInfo().getVersion())
                .build();
    }

    private List<SecurityContext> securityContexts() {
        return Arrays.asList(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build());
    }

    /**
     * 安全认证参数
     *
     * @return
     */
    private List<ApiKey> securitySchemes() {
        List<ApiKey> list = new ArrayList<>();
        list.add(new ApiKey("token", "token", "header"));
        return list;
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{new AuthorizationScope("all", "all scope")};
        return Arrays.asList(new SecurityReference("token", authorizationScopes));
    }

    /**
     * 构建全局参数列表
     *
     * @return
     */
    private List<Parameter> globalParamBuilder() {
        List<Parameter> pars = new ArrayList<>();
        pars.add(parameterBuilder(
                "token",
                "令牌",
                "string",
                "header",
                false)
                .build());
        return pars;
    }

    /**
     * 创建参数
     *
     * @return
     */
    private ParameterBuilder parameterBuilder(String name, String desc, String type, String parameterType, boolean required) {
        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar
                .name(name)
                .description(desc)
                .modelRef(new ModelRef(type))
                .parameterType(parameterType)
                .required(required)
                .build();
        return tokenPar;
    }

    /**
     * 创建全局响应值
     *
     * @return
     */
    private List<ResponseMessage> responseBuilder() {
        List<SwaggerResponseMsg> msgList = swaggerProperties.getResponseMsg();
        if (msgList == null) {
            return Arrays.asList();
        }
        List<ResponseMessage> responseMessageList = new ArrayList<>();
        for (SwaggerResponseMsg msg : msgList) {
            responseMessageList.add(new ResponseMessageBuilder()
                    .code(msg.getCode())
                    .message(msg.getMessage())
                    .build());
        }
        return responseMessageList;
    }
}