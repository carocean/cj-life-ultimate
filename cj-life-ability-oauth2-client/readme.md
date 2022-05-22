# 直接为客户端程序提供鉴权能力
注：此为不经过网关鉴权的客户端，这类客户端直接与认证中心对接。

# 配置
在application.yml中要声明为客户端
```yaml
security:
  #以下必须配置，oauth2将自动去认证服务器取令牌相关的用户及角色
  oauth2:
    client:
      client-id: client1
      preEstablishedRedirectUri:
      client-secret: client1_secret
      access-token-uri: http://localhost:8080/oauth/token
      user-authorization-uri: http://localhost:8080/oauth/authorize
    resource:
      user-info-uri: http://localhost:8080/user
      token-info-uri: http://localhost:8080/oauth/check_token

```
