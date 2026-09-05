# seckill-service

## 连接远程 MySQL 和 Redis

服务器上的 MySQL 和 Redis 只监听 `127.0.0.1`，本地开发通过 SSH 隧道连接，无需在阿里云防火墙中开放 `3306` 或 `6379`。

连接关系：

```text
本机 127.0.0.1:13306  --SSH-->  服务器 127.0.0.1:3306  (MySQL)
本机 127.0.0.1:16379  --SSH-->  服务器 127.0.0.1:6379  (Redis)
```

远程进入 Redis 容器：`docker exec -it redis redis-cli`

### 1. 配置 SSH

编辑 Windows 用户目录下的 SSH 配置：

```powershell
notepad $HOME\.ssh\config
```

添加：

```sshconfig
Host aliyun
    HostName 47.94.228.172
    User root
    ServerAliveInterval 30
    ServerAliveCountMax 3
    ExitOnForwardFailure yes
    LocalForward 13306 127.0.0.1:3306
    LocalForward 16379 127.0.0.1:6379
```

不要把 SSH、MySQL 或 Redis 密码写入 SSH 配置或提交到 Git。

### 2. 启动 SSH 隧道

每天开始本地开发前，在一个单独的 PowerShell 窗口运行：

```powershell
ssh -N aliyun
```

输入 SSH 密码时终端不会显示字符，这是正常行为。认证成功后窗口会保持运行且没有输出。开发期间不要关闭该窗口；结束开发时按 `Ctrl+C` 关闭隧道。

### 3. 验证隧道

另开一个 PowerShell 窗口运行：

```powershell
Test-NetConnection 127.0.0.1 -Port 13306
Test-NetConnection 127.0.0.1 -Port 16379
```

两个结果都应显示：

```text
TcpTestSucceeded : True
```

### 4. 本地连接参数

本地项目或数据库客户端使用以下地址：

| 服务  | 主机        |    端口 | 数据库/用户                    |
| ----- | ----------- | ------: | ------------------------------ |
| MySQL | `127.0.0.1` | `13306` | 数据库 `seckill_service`，用户 `appuser` |
| Redis | `127.0.0.1` | `16379` | 默认数据库 `0`                 |

密码从服务器 `/opt/database-stack/.env` 中获取，不要复制到 README 或提交到 Git：

```bash
ssh aliyun
cd /opt/database-stack
cat .env
```

### 5. 项目本地配置

项目本地开发读取 `src/main/resources/application-local.yml`。该文件已被 `.gitignore` 忽略；可以从 `application-local.example.yml` 复制后填写服务器数据库和 Redis 密码。

本地连接参数应为：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:13306/seckill_service?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: appuser
    password: <服务器 .env 中的 MYSQL_PASSWORD>

  data:
    redis:
      host: 127.0.0.1
      port: 16379
      password: <服务器 .env 中的 REDIS_PASSWORD>
      database: 0
```

如果使用 `application-prod.yml` 启动，需提供它实际读取的变量：

```dotenv
DB_URL=jdbc:mysql://127.0.0.1:13306/seckill_service?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
DB_USERNAME=appuser
DB_PASSWORD=<服务器 .env 中的 MYSQL_PASSWORD>
```

### 常见问题

- `Connection refused`：确认 `ssh -N my-server` 窗口仍在运行，并确认服务器上的容器已启动。
- SSH 再次显示 `Password:`：SSH 密码输入错误。
- 本地端口被占用：运行 `Get-NetTCPConnection -LocalPort 13306,16379` 查找占用进程，或修改 SSH 配置中的本地端口。
- 查看服务器容器：登录服务器后运行 `cd /opt/database-stack && docker compose ps`。
