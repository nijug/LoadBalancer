# Build stage
FROM gradle:jdk8-jammy AS build
WORKDIR /app
COPY ./build.gradle .
COPY ./src/ ./src/
RUN gradle buildLoadBalancer test --no-daemon

# Final stage
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./LoadBalancer.jar
ENTRYPOINT ["java","-jar","LoadBalancer.jar"]
CMD ["$SCHED_ALGO", "$NUM_WORKERS", "$LB_PORT", "$WORKER_START_PORT"]