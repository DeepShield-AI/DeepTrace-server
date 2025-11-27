## Docker 容器化部署方案
### 一、部署机的准备
### 1. 创建项目目录
```sh
mkdir DeepTrace_Project
cd DeepTrace_Project
```

### 2. 拉取最新项目源码
```sh
git clone --single-branch --branch main https://github.com/DeepShield-AI/DeepTrace-server.git
```

### 3. 确保部署机已安装 Docker 与 Docker-Compose
```sh
git clone --single-branch --branch main https://github.com/DeepShield-AI/DeepTrace-server.git
```
### 二、准备项目相关文件配置
### 1. 确保根目录下有Dockerfile文件和以下内容
```sh
# 构建阶段
FROM registry.cn-hangzhou.aliyuncs.com/acs/maven:latest AS builder
COPY settings.xml /root/.m2/settings.xml
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# 运行阶段
FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6
WORKDIR /app
COPY --from=builder /app/target/*.jar deepserver.jar
CMD ["java", "-jar", "app.jar"]
```

**注意**：如果项目没有 `settings.xml`，可以删除 `COPY settings.xml ...` 这一行。

### 2. 根目录下创建.dockerignore文件，内容如下（可选，推荐）
```sh
# 排除不需要打包进镜像的文件，减小构建上下文大小，加快构建速度
target/
.idea/
*.iml
.git/
.gitignore
README.md
```
### 三、构建、传输、部署镜像
### 1. 使用 Docker Compose 部署

```sh
# 1.在根目录下创建 docker-compose.yml 文件，内容如下：
version: '3.8'

services:
  app:
    # 如果你想在服务器上直接构建，使用 build
    build: .
    # 如果镜像已经存在于服务器（或镜像仓库），使用 image
    # image: deep-server:latest 
    container_name: deep-server
    ports:
      - "80:8080"  # 将服务器的80端口映射到容器的8080端口
    restart: unless-stopped # 容器异常退出时自动重启
```

### 2.将项目上传到服务器（以云主机114.215.254.187为例）：
```sh
scp -r DeepTrace_Project root@114.215.254.187:~/wzh/DeepTrace/
```
### 3.在服务器上构建并运行容器：
```sh
ssh root@114.215.254.187
cd ~/wzh/DeepTrace/DeepTrace_Project

# 执行 `docker-compose` 命令：
# （-d 表示后台运行，--build：每次启动前都会重新构建镜像，适合开发阶段）
docker-compose up -d --build
```

### 四、验证和管理
### 1. 验证服务是否启动成功
```sh
# 查看正在运行的容器
docker ps

# 查看容器日志（实时查看）
docker logs -f deep-server
# （若启用 docker-compose）
docker-compose logs -f

# 停止并删除容器
docker stop deep-server
docker rm deep-server
#（若启用 docker-compose，在项目目录下执行）
docker-compose down
```