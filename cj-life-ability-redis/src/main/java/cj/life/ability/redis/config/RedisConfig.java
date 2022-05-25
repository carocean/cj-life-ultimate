package cj.life.ability.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@ComponentScan(basePackages = {"cj.life.ability.redis"})
public class RedisConfig {
//    @Value("${spring.redis.sentinel.master}")
//    private String master;
//    @Value("#{'${spring.redis.sentinel.nodes}'.split(',')}")
//    private List<String> nodes;
//    @Value("${spring.redis.sentinel.password}")
//    private String password;


//    @Bean
//    @ConfigurationProperties(prefix="spring.redis")
//    public JedisPoolConfig getRedisConfig(){
//        JedisPoolConfig config = new JedisPoolConfig();
//        return config;
//    }
//    @Bean
//    public RedisSentinelConfiguration sentinelConfiguration(){
//        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
//        //配置matser的名称
//        redisSentinelConfiguration.master(master);
////        logger.info("redis password --------------------------->"+password);
//        redisSentinelConfiguration.setPassword(password);
//        //配置redis的哨兵sentinel
//        Set<RedisNode> redisNodeSet = new HashSet<>();
//        nodes.forEach(x->{
//            redisNodeSet.add(new RedisNode(x.split(":")[0],Integer.parseInt(x.split(":")[1])));
//        });
////        logger.info("redisNodeSet --------------------------->"+redisNodeSet);
//        redisSentinelConfiguration.setSentinels(redisNodeSet);
//        return redisSentinelConfiguration;
//    }

//    @Bean
//    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig, RedisSentinelConfiguration sentinelConfig) {
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfig,jedisPoolConfig);
//        String password = jedisConnectionFactory.getPassword();
////        logger.info("redis密码：------------------>"+password);  //此处也能获取到密码
//        return jedisConnectionFactory;
//    }
    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }
}