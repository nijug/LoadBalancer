# Build stage
FROM gradle:jdk8-jammy AS build
WORKDIR /app
COPY ./build.gradle .
COPY ./src/main/java/org/example/LoadBalancer/ ./src/main/java/org/example/LoadBalancer/
RUN gradle clean buildLoadBalancer --info

# Final stage
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./LoadBalancer.jar
ENTRYPOINT ["sh", "-c"]
CMD ["sleep 15 && java -jar LoadBalancer.jar $SCHED_ALGO $NUM_WORKERS $LB_PORT $WORKER_START_PORT"]