FROM maven:3.6.3-jdk-11 AS builder
ENV MAVEN_OPTS="-Dmaven.repo.local=/usr/share/maven/ref/repository -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -Djava.awt.headless=true -Dmaven.repo.remote=https://maven.aliyun.com/repository/public/"

WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6
WORKDIR /app
COPY --from=builder /app/target/*.jar deepserver.jar
EXPOSE 8080
CMD ["java", "-jar", "deepserver.jar"]