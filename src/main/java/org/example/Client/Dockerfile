# Build stage
FROM gradle:jdk8-jammy AS build
WORKDIR /app
COPY ./build.gradle .
COPY ./src/ ./src/
RUN gradle buildClient --no-daemon

# Final stage
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./Client.jar
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar Client.jar $LB_PORT $WORKER_START_PORT"]