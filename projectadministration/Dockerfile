# Build the Jar

FROM gradle:5.6-jdk11 AS builder

USER root

WORKDIR /builder

COPY . /builder

RUN gradle bootJar --stacktrace

# Build the executable image

FROM openjdk:11.0.4

#COPY build/libs/employee-administration-0.0.1-SNAPSHOT.jar /usr/src/app/app.jar
COPY --from=builder /builder/build/libs/projectadministration-0.0.1-SNAPSHOT.jar /usr/src/app/app.jar

WORKDIR /usr/src/app

EXPOSE 8090

ENV SPRING_PROFILES_ACTIVE prod
ENV KAFKA_URL kafka:9092

#CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod", "--spring.kafka.consumer.bootstrap-servers=kafka:9092", "--spring.kafka.bootstrap-servers=kafka:9092"]
CMD ["java", "-jar", "app.jar"]