FROM gradle:7.4-jdk18-alpine as builder

COPY . /home/gradle
WORKDIR /home/gradle
RUN cd /home/gradle
RUN gradle build -x test

FROM openjdk:18.0.1.1-jdk-slim-buster
COPY --from=builder /home/gradle/build/libs/tagged-image-1.0.0-SNAPSHOT.war tagged-image.war

ENTRYPOINT ["java", "-jar", "/tagged-image.war"]