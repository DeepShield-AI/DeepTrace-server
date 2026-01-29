FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6
COPY target/start-0.0.1-SNAPSHOT.jar deeptrace.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/deeptrace.jar"]
