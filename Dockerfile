FROM registry.cn-hangzhou.aliyuncs.com/library/maven:3.8.4-openjdk-17 AS builder
WORKDIR /app

COPY settings.xml .
COPY pom.xml .

RUN mvn -s settings.xml dependency:go-offline -B

COPY src ./src
RUN mvn -s settings.xml package -DskipTests && \
    ls -l target/  # 查看生成的JAR

FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6
WORKDIR /app
COPY --from=builder /app/target/middle-0.0.1-SNAPSHOT.jar deepserver.jar
EXPOSE 8080
CMD ["java", "-jar", "deepserver.jar"]