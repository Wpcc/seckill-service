# Day105：账号与商品模块复盘

## 已实现接口

| 接口 | 是否需要登录 | 作用 |
| --- | --- | --- |
| POST /api/users/register | 否 | 注册用户 |
| POST /api/auth/login | 否 | 校验密码并签发 JWT |
| GET /api/auth/me | 是 | 获取当前登录用户 ID |
| GET /api/products | 是 | 获取上架商品列表 |
| GET /api/products/{id} | 是 | 获取单个上架商品详情 |

## JWT 请求流程

```text
客户端登录
→ 服务端校验 BCrypt 密码
→ 服务端签发 JWT
→ 客户端在 Authorization: Bearer <token> 中携带 Token
→ 拦截器校验 Token
→ CurrentUserContext 保存 userId
→ Controller / Service 执行业务
→ 请求结束后清理 ThreadLocal
```

## 接口验证结果

- 注册：重复用户名时返回 500，消息为“用户名已存在”。当前尚未接入全局异常处理器；后续会将此类业务异常映射为合适的 4xx 响应。
- 登录：返回用户 ID、用户名、`tokenType`、过期时间和访问令牌。访问令牌属于 Bearer 凭证，文档中不保存真实值。
- 未携带 Token 查询商品：返回 401，消息为“缺少或无效的登录令牌”。
- 携带 Token 查询商品：返回 200，成功获取上架商品。
- 查询不存在商品：返回 404，当前响应体为空。
- 查询当前用户：返回 200 与当前 `userId`。

## 当前项目分层

```text
Controller：接收 HTTP 请求、返回 HTTP 响应
Service：处理业务规则和 DTO 转换
Mapper：执行数据库查询
DTO：定义接口输入与输出
Interceptor：统一完成 JWT 鉴权
CurrentUserContext：在一次请求中保存当前用户 ID
```
