package cj.life.ability.oauth2.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisTenantStore implements ITenantStore {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String TENANT_KEY = "tenant_on_user_client:%s:%s";

    @Override
    public String readTenantId(String user, String client_id) {
        String key = String.format(TENANT_KEY, user, client_id);
        String tenant = redisTemplate.opsForValue().get(key);
        return tenant;
    }
}
