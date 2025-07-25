FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests
COPY /app/target/middle-0.0.1-SNAPSHOT.jar deepserver.jar
EXPOSE 8080
CMD ["java","-jar","/deepserver.jar"]