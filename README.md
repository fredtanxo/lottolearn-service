# LotToLearn 网课平台

**后端工程** | [前端工程](https://github.com/fredtanxo/lottolearn-web) | [附加工程](https://github.com/fredtanxo/lottolearn-other)

LotToLearn 是一个简易的网课平台。

## 功能

* GitHub 第三方登录
* 通过邀请码加入课程
* 发布、查看课程公告
* 对课程评分
* 创建、学习课程章节
* 附加视频、文件到章节
* 参与章节讨论、点赞讨论
* 音视频直播、即时聊天
* 限时签到、随机提问
* 热门课程

## 主要模块

* **auth**：用户登录鉴权和授权、令牌刷新
* **course**：课程、章节、公告、签到
* **message**：直播聊天、系统消息
* **processor**：文件处理
* **storage**：文件上传下载
* **system**：日志记录、数据统计
* **user**：用户、角色、权限管理

## 部署并运行

> 在 **localhost** 运行的示例

0. 环境准备

   | 名称     | 版本   | 要求 |
   | -------- | ------ | ---- |
   | JDK      | 14     | 必须 |
   | MySQL    | 8.0.21 | 推荐 |
   | Redis    | 6.0.6  | 推荐 |
   | RabbitMQ | 3.8.3  | 推荐 |
   | Nacos    | 1.3.2  | 推荐 |
   | nginx    | 1.19.1 | 推荐 |

1. 必要配置

   * hosts

     附加 [hosts.example](config/hosts.example) 的内容到本地 hosts 文件

     ```shell
     $ sudo cat hosts.example >> /etc/hosts
     ```

   * nginx

     附加 [nginx.conf.example](config/nginx.conf.example) 到 nginx 配置文件

     ```shell
     $ tail -2 /usr/local/etc/nginx/nginx.conf
         include servers/*;
     }
     $ cp nginx.conf.example /usr/local/etc/nginx/servers/lottolearn.conf
     ```

   * SSL

     使用 [mkcert](https://github.com/FiloSottile/mkcert) 签发一个本地证书

     ```shell
     $ mkcert lottolearn.com "*.lottolearn.com" lottolearn.com localhost 127.0.0.1 ::1
     ```

     将生成的密钥放置到 nginx 配置目录下

     ```shell
     $ cp lottolearn.com+4-key.pem lottolearn.com+4.pem /usr/local/etc/nginx/
     ```

2. 启动 MySQL、Redis、RabbitMQ、Nacos、nginx

   ```shell
   $ mysql.server start
   $ redis-server
   $ /usr/local/sbin/rabbitmq-server
   $ ~/Downloads/nacos/bin/startup.sh -m standalone
   $ nginx
   ```

3. 根据每个模块的 `application.yml` 配置启动参数

   

4. 按顺序启动模块

   ```
   user -> auth -> system -> (course | message | processor | storage)
   ```

5. 启动 [前端项目](https://github.com/fredtanxo/lottolearn-web) 和 [附加项目](https://github.com/fredtanxo/lottolearn-other)

## License

MIT
