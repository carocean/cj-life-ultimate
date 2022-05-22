package cj.life.ability.oauth2.redis;

import cj.life.ability.redis.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author: cj
 * @create:
 * @description:
 */
@Component
@ConditionalOnBean({RedisConfig.class})
public class RedisAuthCodeStoreServices extends RandomValueAuthorizationCodeServices {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final String CODE_KEY = "auth:code:%s";

    /**
     * 存储code
     *
     * @param code
     * @param authentication
     */
    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        String key = String.format(CODE_KEY, code);
        System.out.println("保存code：" + code);
        //保存30分钟
        redisTemplate.opsForValue().set(key, SerializationUtils.serialize(authentication), 30, TimeUnit.MINUTES);
    }

    /**
     * 删除code
     *
     * @param code
     * @return
     */
    @Override
    protected OAuth2Authentication remove(String code) {
        String key = String.format(CODE_KEY, code);
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            System.out.println("删除code：" + code);
            redisTemplate.delete(key);
            return SerializationUtils.deserialize((byte[]) value);
        }
        return null;
    }
}
