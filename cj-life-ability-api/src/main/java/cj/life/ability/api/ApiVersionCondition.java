package cj.life.ability.api;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: ApiVersionCondition
 * @Desc: 版本号匹配筛选器
 * @version: 1.0
 * @author: loop.fu
 * @date: 2021/8/9 8:46
 */
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {

    /**
     * 路径中版本的前缀， 这里用 v[1-9]的形式
     */
    private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile("v(\\d+)/");

    private int apiVersion;

    public ApiVersionCondition(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    public ApiVersionCondition combine(ApiVersionCondition other) {
        /** 采用最后定义优先原则，则方法上的定义覆盖类上面的定义*/
        return new ApiVersionCondition(other.getApiVersion());
    }

    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        Matcher m = VERSION_PREFIX_PATTERN.matcher(request.getRequestURI());
        if (m.find()) {
            Integer version = Integer.valueOf(m.group(1));
            /** 如果请求的版本号大于配置版本号,则满足*/
            if (version >= this.apiVersion)
                return this;
        }
        return null;
    }

    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        /** 优先匹配最新的版本号*/
        return other.getApiVersion() - this.apiVersion;
    }

    public int getApiVersion() {
        return apiVersion;
    }

}