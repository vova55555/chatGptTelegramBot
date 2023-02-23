FROM amazoncorretto:19-alpine
COPY target/chatgpt-bot*.jar /app/myapp.jar
WORKDIR /app
CMD ["java", "-jar", "/app/myapp.jar"]
