FROM openjdk:14-jdk-alpine
COPY ./target/*.jar /usr/local/lib/app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/usr/local/lib/app.jar"]