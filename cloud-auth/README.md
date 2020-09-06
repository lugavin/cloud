# cloud-auth

## 登录

- 服务端: DB => RefreshToken & Redis => {roleCode:permCode:permission}
- 客户端: LocalStorage => AccessToken & RefreshToken

## 认证

- 会话验证: 验证Token是否合法, 并支持是否需要会话的一个URL配置, 如 `忘记密码` 并不需要会话
- 权限验证: 通过AOP实现, 在Controller相应方法上添加自定义注解来进行权限验证

## 参考链接

- [Auth with JWT Refresh Token](https://solidgeargroup.com/en/refresh-token-with-jwt-authentication-node-js/)