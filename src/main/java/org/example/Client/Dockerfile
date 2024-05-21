# Build stage
FROM gradle:jdk8-jammy AS build
WORKDIR /app
COPY ./build.gradle .
COPY ./src/main/java/org/example/Client/ ./src/main/java/org/example/Client/
COPY ./src/main/java/org/example/Request/ ./src/main/java/org/example/Request/
RUN gradle clean buildClient --info

# Final stage
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./Client.jar
ENTRYPOINT ["sh", "-c"]
CMD ["sleep 16 && java -jar Client.jar $NUM_CLIENTS $LB_PORT"]