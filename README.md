# Prototype for an event-based Microservice ERP System

This repository contains an ERP Prototype built as a microservice architecture. Each service uses its own schema in a PostgreSQL database. The services implement the CQRS and Saga Pattern. Communication is handled asynchronously using events with Apache Kafka as the event broker.

## How to run

Run the services using Docker.

### Compose
Run `docker-compose build` to build the service images. Afterwards run `docker-compose up` with or without `-d` and wait until all services are running. If you have trouble building the services you can replace the `build` lines in the `docker-compose.yml` with the images used in `docker-compose-img.yml`. Stop the application by running `docker-compose down`.

### Swarm
To start a scaled swarm first initialize a swarm with `docker swarm init`. Then deploy the stack defined in the second compose file running `docker stack deploy -c docker-compose-img.yml <STACK NAME>`. Use any name you want for the stack. To stop the application remove the stack using `docker stack rm <STACK NAME>` and leave the swarm with `docker swarm leave -f`.
