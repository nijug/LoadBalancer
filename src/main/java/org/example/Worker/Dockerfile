# Build stage
FROM gradle:jdk8-jammy AS build
WORKDIR /app
COPY ./build.gradle .
COPY ./src/ ./src/
RUN gradle buildWorkerManager test --no-daemon

# Final stage
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./WorkerManager.jar
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar WorkerManager.jar $NUM_WORKERS $WORKER_START_PORT"]