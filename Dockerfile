FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17
VOLUME /home/deepserver
ADD middle-0.0.1-SNAPSHOT.jar deepserver.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/deepserver.jar"]