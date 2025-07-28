FROM  docker.xuanyuan.me/library/maven:3.9.11-ibm-semeru-17-noble AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6
WORKDIR /app
COPY --from=builder /app/target/*.jar deepserver.jar
EXPOSE 8080
CMD ["java", "-jar", "deepserver.jar"]