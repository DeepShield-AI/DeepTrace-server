<h1 align="center">DeepTrace 部署文档</h1>

本仓库为 [DeepTrace-server](https://github.com/DeepShield-AI/DeepTrace-server.git) 的代码，提供 Linux运行环境准备 及 [Docker部署](README-docker.md) 和 直接部署 方案 。

<!-- <p align="center"><img src="https://github.com/hiroi-sora/DeepTrace/assets/56373419/a300661e-0789-40bd-a3d6-41121c276e50" alt="预览.png" style="width: 80%;"></p> -->

### 已通过测试的系统

主机部署：
- Ubuntu `22.04`

[//]: # (Docker 部署：)

[//]: # (- CentOS `7`？)

### DeepTrace 当前功能

功能模块：

- [x] 视图列表（自定义视图、内置视图）
- [x] 应用观测（调用链追踪、调用链拓扑、日志检索）
- [x] 指标中心（指标查看、指标摘要、指标模板）
- [x] 系统管理（采集器）
- [x] 告警管理（事件列表、事件分析、告警策略）

受限的功能和暂时问题：

- [ ] 暂无登录步骤
- [ ] 暂不支持 视图列表模块、应用观测下的日志检索、指标中心下的指标摘要和指标模板、告警管理下的告警策略
- [ ] 缺失cdn，初次进入网页响应慢

### 硬件要求

> [!NOTE]
> 当前 DeepTrace-Linux ...，未来...。  

检查CPU兼容性：

```sh
lscpu | grep xxx
```

...

```
... ...
```

**如果看不到任何输出，这表明当前CPU不支持...，暂时无法部署使用 DeepTrace 。**

## Docker 部署方案

👉 [README-docker](README-docker.md)

## 直接部署方案

### 1. 创建项目目录

```sh
mkdir DeepTrace_Project
cd DeepTrace_Project
```

### 2. 拉取最新源码

```sh
git clone --single-branch --branch main https://github.com/DeepShield-AI/DeepTrace-server.git
```

### 2. 配置Elasticsearch、MySql

```sh
按路径找到文件： start/src/main/resources/application.properties
- 配置 Elasticsearch 数据库连接、端口地址，如：
spring.elasticsearch.uris=http://localhost:9200
```

### 3. 项目构建

```sh
回到根目录DeepTrace-server下，
- 推荐：整体构建：
mvn clean package

- 只构建启动模块（节省时间）：
mvn -pl start -am clean package

构建完成后，jar 位于 `start/target/start-0.0.1-SNAPSHOT.jar`
```

### 4. 上传jar包至目标服务器

```sh
上传到目标服务器（以云主机114.215.254.187为例）：
scp D:\pj\Experiment_Platform\DeepTrace-server\start\target\start-0.0.1-SNAPSHOT.jar root@114.215.254.187:~/wzh/DeepTrace/start-0.0.1-SNAPSHOT.jar                                   
```

### 5. 运行jar

```sh
java -jar start/target/start-0.0.1-SNAPSHOT.jar

之后运行ps -ef | grep start-0.0.1-SNAPSHOT.jar，查看运行情况，确定项目已启动                           
```

<a id="venv"></a>

### （可选）编辑器

- 如果需要对代码进行二次开发或调试，推荐使用 [IntelliJ IDEA](https://www.jetbrains.com.cn/) 编辑器。
- 插件推荐：
  - [Java](https://marketplace.visualstudio.com/items?itemName=ms-Java.Java)
  - [Black Formatter](https://marketplace.visualstudio.com/items?itemName=ms-Java.black-formatter) （Java规范格式化）
  - [QML](https://marketplace.visualstudio.com/items?itemName=bbenoist.QML) （提供qml语法高亮）
  - [QML Snippets](https://marketplace.visualstudio.com/items?itemName=ThomasVogelpohl.vsc-qml-snippets) （提供qml代码补全）

[//]: # (- 本仓库提供了 `.IJ` 项目配置文件。)

---

## 关于项目结构

### 各仓库：

- [主仓库](https://github.com/DeepShield-AI/DeepTrace-server)
- [插件库](...)
- [Windows 运行库]？
- [Linux 运行库]？ 👈

### 工程结构：

`**` 后缀表示本仓库(`Linux 运行库`)包含的内容。

```
DeepTrace-server
├─ deeptrace-insfrastructure
├─ deeptrace-search
├─ deeptrace-service
└─ start
```