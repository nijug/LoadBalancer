# Build stage
FROM gradle:jdk8-jammy AS build
WORKDIR /app
COPY ./build.gradle .
COPY ./src/main/java/org/example/Worker/ ./src/main/java/org/example/Worker/
COPY ./src/main/java/org/example/Request/ ./src/main/java/org/example/Request/
RUN gradle clean buildWorkerManager

# Final stage
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./WorkerManager.jar
ENTRYPOINT ["sh", "-c"]
CMD ["sleep 10 && java -jar WorkerManager.jar $NUM_WORKERS $WORKER_START_PORT"]