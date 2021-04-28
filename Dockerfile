FROM openjdk:8-jdk
ENV TZ='Asia/Shanghai'
VOLUME /tmp
ADD target/unnetdisk-server-1.0.0.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
