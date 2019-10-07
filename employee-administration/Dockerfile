FROM openjdk:11.0.4

COPY build/libs/employee-administration-0.0.1-SNAPSHOT.jar /usr/src/app/app.jar

WORKDIR /usr/src/app

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE prod
ENV KAFKA_URL kafka:9092

#CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod", "--spring.kafka.consumer.bootstrap-servers=kafka:9092", "--spring.kafka.bootstrap-servers=kafka:9092"]
CMD ["java", "-jar", "app.jar"]