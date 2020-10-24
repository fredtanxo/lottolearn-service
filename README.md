# LotToLearn 网课平台

**后端工程** | [前端工程](https://github.com/fredtanxo/lottolearn-web) | [附加工程](https://github.com/fredtanxo/lottolearn-other)

LotToLearn 是一个简易的网课平台。

## 功能

* 第三方登录（GitHub、微博）
* 创建课程（可自由创建）
* 课程身份（每门课程身份独立）
* 加入课程（邀请码机制）
* 课程章节（可附加视频和文件，有独立的讨论区）
* 课程直播（语音、视频、屏幕共享、即时聊天、限时签到、随机提问）
* 课程公告（支持HTML格式、通知成员查看）
* 闪电记录（随时记录想法、笔记、视频帧、视频片段）
* 热门课程（根据点击量定时更新）

# 模块

* [auth](https://github.com/fredtanxo/lottolearn-service/tree/master/auth) ：用户登录鉴权授权、令牌刷新
* [course](https://github.com/fredtanxo/lottolearn-service/tree/master/course) ：课程、章节、公告、签到
* [message](https://github.com/fredtanxo/lottolearn-service/tree/master/message) ：直播聊天、系统消息
* [processor](https://github.com/fredtanxo/lottolearn-service/tree/master/processor) ：视频转码
* [storage](https://github.com/fredtanxo/lottolearn-service/tree/master/storage) ：文件存储、上传下载
* [system](https://github.com/fredtanxo/lottolearn-service/tree/master/system) ：日志记录、数据统计
* [user](https://github.com/fredtanxo/lottolearn-service/tree/master/user) ：用户、账户、角色、权限管理

## 架构

![LotToLearn 项目架构](http://assets.processon.com/chart_image/5f92fe98e0b34d07111ae3da.png?_=1603504291588)

## 技术栈

* 服务侧
  * Spring Boot
  * Dubbo
  * Spring Data
  * MyBatis
  * H2
  * MyBatis-PageHelper
  * Spring Security
  * Spring Messaging
  * Fastjson
  * Protostuff
  * Springfox
  * Apache POI
* 中间件
  * MySQL
  * Redis
  * RabbitMQ
  * Nacos

## 文档

参见 [GitHub Wiki](https://github.com/fredtanxo/lottolearn-service/wiki)

## License

[MIT](https://github.com/fredtanxo/lottolearn-service/blob/master/LICENSE)
