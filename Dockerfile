FROM maven:3.9.6-jdk-17 AS builder
RUN mkdir -p /etc/docker && \
    echo '{"registry-mirrors":["https://xb7xmcmw.mirror.aliyuncs.com"]}' > /etc/docker/daemon.json
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6
WORKDIR /app
COPY --from=builder /app/target/*.jar deepserver.jar
EXPOSE 8080
CMD ["java", "-jar", "deepserver.jar"]