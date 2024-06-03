# Spring Boot Kafka Streams Processor Demo

## Overview
This project is a Spring Boot microservice that leverages Kafka Streams for real-time data processing. It includes various configurations, controllers, services, and Kafka Streams topologies designed to handle and process streaming data efficiently. 

## scripts
* `run.bat`: Batch script to run the application on Windows.
* `run.sh`: Shell script to run the application on Unix-like systems.

## Prerequisites
* Java 17 or higher
* Maven
* Docker (optional, for running Docker Compose which include Zookeeper and Apache Kafka)

## Running the Application
### Using Docker Compose
1. **Start Kafka and Zookeeper**:
   ```sh
   docker-compose up
   ```
   
2. **Build and Run the Application**:
   ```sh
   mvn clean package spring-boot:run
   ```

Endpoints



	•	GET /api/resource - Get all resources

	•	POST /api/resource - Create a new resource

	•	PUT /api/resource/{id} - Update a resource


3. **calling endpoints**:
   ```sh
   ./scripts/run.sh
   ```
   or for Windows:
   ```sh
   scripts\run.bat
   ```