## 项目信息
- **码如云**是一个基于二维码的一物一码管理平台，可用于固定资产管理，设备巡检和问卷调查等众多场景。在技术上，码如云是一个无代码平台，全程采用DDD、事件驱动架构和整洁架构思想完成开发。
- 与本项目配套的码如云前端代码请访问：[https://github.com/mryqr-com/mry-frontend](https://github.com/mryqr-com/mry-frontend)

## 在线免费使用
- 您可以访问[https://www.mryqr.com](https://www.mryqr.com)免费使用码如云在线服务
- 我们正在寻找能够提供微信公众号（需要是**服务号**类型）的合作方，以让码如云能够入驻在该公众号中，从而为用户提供更多更完善的功能，有意者可联系作者

## 本地运行
- 先确保本地安装的是Java 17及以上版本
- 本地启动：`./local-run.sh`，该命令将通过docker-compose自动运行MongoDB和Redis，再启动Spring Boot主程序，启动后访问 http://localhost:8080/about ，如可正常访问则表示启动成功
- 本地构建：`./ci-build.sh`，该命令将通过docker-compose自动运行MongoDB和Redis，再运行单元测试，API测试以及动态代码检查等构建步骤
- 如需在本地进行前后端联调，请参考[码如云前端](https://github.com/mryqr-com/mry-frontend)

## 所有命令

| 功能                 | 命令                                                                                   | 说明                                       |
|--------------------|--------------------------------------------------------------------------------------|------------------------------------------|
| 在IntelliJ中打开工程     | `./idea.sh`                                                                          | 将自动启动IntelliJ，无需另行在IntelliJ中做导入操作        |
| 本地启动               | `./local-run.sh`                                                                     | API端口：8080, 调试端口：5005                    |
| 清空所有本地数据后再启动       | `clear-and-local-run.sh`                                                                | API端口：8080, 调试端口：5005                    |
| 本地构建               | `./ci-build`                                                                         | 将运行单元测试，API测试以及静态代码检查                    |
| 单独停止docker-compose | `./gradlew composeDown`                                                              | 将清除所有本地数据，包括MongoDB和Redis                |
| 单独启动docker-compose | `./gradlew composeUp`                                                                | 通过docker-compose启动MongoDB和Redis，如已经启动则跳过 |


## 技术栈
- Java 17，Spring Boot 3，MongoDB 4.x，Redis 6.x等


## 赞赏作者

如果您认为本代码库能为您带去用处与价值，您可微信扫描以下二维码赞赏作者，以请作者喝杯咖啡。

![赞赏作者](./donation.jpeg)


