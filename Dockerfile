# build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN apk add --no-cache ca-certificates && update-ca-certificates && ./mvnw -q -DskipTests clean package

# run
FROM eclipse-temurin:17-jre-alpine
ENV TZ=Europe/Moscow
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT sh -c "java $JAVA_OPTS -jar /app/app.jar"
