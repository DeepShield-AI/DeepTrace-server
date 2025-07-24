# 第一阶段：构建应用
FROM maven:3.8.4-openjdk-11 AS builder
WORKDIR /app

# 复制项目文件
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 第二阶段：运行应用
FROM openjdk:11-jre-slim
WORKDIR /app

# 从构建阶段复制生成的JAR文件
COPY --from=builder /app/target/*.jar /app/app.jar

# 暴露应用端口（根据实际情况修改）
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "app.jar"]