FROM openjdk:14-jdk-alpine
COPY ./target/*.jar /usr/local/lib/app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/usr/local/lib/app.jar"]