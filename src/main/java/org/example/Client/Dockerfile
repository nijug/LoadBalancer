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
ENTRYPOINT ["java","-jar","Client.jar"]
CMD ["$NUM_CLIENTS", "$CLIENT_PORT"]