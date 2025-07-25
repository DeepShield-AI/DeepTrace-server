FROM registry.cn-hangzhou.aliyuncs.com/acs/maven:latest AS builder
COPY settings.xml /root/.m2/settings.xml

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