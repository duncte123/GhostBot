FROM adoptopenjdk:15-jdk-hotspot AS builder

WORKDIR /ghostbot
COPY gradle ./gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN ./gradlew --no-daemon dependencies
COPY . .
RUN ./gradlew --no-daemon build

FROM adoptopenjdk:15-jre-hotspot

WORKDIR /ghostbot
COPY --from=builder /ghostbot/build/libs/GhostBot.jar ./ghostbot.jar

CMD ["java", "-jar", "ghostbot.jar"]
