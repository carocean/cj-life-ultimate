spring:
  redis:
    #singleten|sentinel|cluster
    database: ${cj.life.spring.redis.db:0} # Redis Database index （ The default is 0）
    #    host:  ${cj.life.spring.redis.host:localhost} # Redis Server address
    port: ${cj.life.spring.redis.port:6379} # Redis Server connection port
    password: ${cj.life.spring.redis.password:123456} # Redis Server connection password （ The default is empty. ）
    timeout: ${cj.life.spring.redis.timeout:5000} # Connection timeout , Company ms
    sentinel:
      master: ${cj.life.spring.redis.sentinel.master:local-master}
      nodes: ${cj.life.spring.redis.sentinel.nodes:localhost:26379,localhost:26380}
    lettuce:
      pool:
        max-active: 8 # Maximum number of connections in connection pool （ Use a negative value to indicate that there is no limit ） Default 8
        max-wait: -1 # Connection pool maximum blocking wait time （ Use a negative value to indicate that there is no limit ） Default -1
        max-idle: 8 # The maximum free connection in the connection pool Default 8
        min-idle: 0 # The smallest free connection in the connection pool Default 0