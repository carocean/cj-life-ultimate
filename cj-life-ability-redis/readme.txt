开发环境：

spring:
  redis:
    #在开发环境只能用单例模式，原因是：如用哨兵模式由于哨兵是部署在docker中的，其获取的地址为容器内地址，因此连接不成功。
    #但在生产环境，由于本应用作为docker也被部署到和redis的docker同网络下，因此生产环境以sentinel部署。
    #singleten|sentinel|cluster
    mode: sentinel
    database: ${cj.life.spring.redis.db:0} # Redis Database index （ The default is 0）
    host:  ${cj.life.spring.redis.host:localhost} # Redis Server address
    port: ${cj.life.spring.redis.port:6379} # Redis Server connection port
    password: ${cj.life.spring.redis.password:123456} # Redis Server connection password （ The default is empty. ）
    timeout: ${cj.life.spring.redis.timeout:5000} # Connection timeout , Company ms
    lettuce:
      pool:
        max-active: 8 # Maximum number of connections in connection pool （ Use a negative value to indicate that there is no limit ） Default 8
        max-wait: -1 # Connection pool maximum blocking wait time （ Use a negative value to indicate that there is no limit ） Default -1
        max-idle: 8 # The maximum free connection in the connection pool Default 8
        min-idle: 0 # The smallest free connection in the connection pool Default 0

生产环境：

spring:
  redis:
    #singleten|sentinel|cluster
    mode: sentinel
    database: ${cj.life.spring.redis.db:0} # Redis Database index （ The default is 0）
    password: ${cj.life.spring.redis.password:123456} # Redis Server connection password （ The default is empty. ）
    timeout: ${cj.life.spring.redis.timeout:5000} # Connection timeout , Company ms
    sentinel:
      master: ${cj.life.spring.redis.sentinel.master:local-master}
      nodes: ${cj.life.spring.redis.sentinel.nodes:redis-server-master:26379,redis-server-slave-1:26379,redis-server-slave-2:26379}
    lettuce:
      pool:
        max-active: 8 # Maximum number of connections in connection pool （ Use a negative value to indicate that there is no limit ） Default 8
        max-wait: -1 # Connection pool maximum blocking wait time （ Use a negative value to indicate that there is no limit ） Default -1
        max-idle: 8 # The maximum free connection in the connection pool Default 8
        min-idle: 0 # The smallest free connection in the connection pool Default 0