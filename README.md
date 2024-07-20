# Spring Boot and Kafka Streams Processor Demo


## Overview
This project is a Spring Boot microservice that leverages **Kafka Streams Processor API** for real-time data processing. 

It includes configurations, controllers, services, and Kafka Streams topologies designed to handle and process streaming data efficiently for two use cases.
* Stateful Session Management with Custom Expiration Logic
* Stateful Anomaly Detection with Dynamic Thresholds


## Stateful Session Management with Custom Expiration Logic
This scenario is a stateful stream processing for session management where sessions are managed with custom expiration logic. 


The input record for this use case is a key-value pair, with the key being the user ID and the value being some string about user activity.
By calling the following endpoint an event will be produced for user activity:
```sh
curl POST /v1/api/user/activity 
{
  "userId": "1",
  "activity": "new activity added"
}
```
If the time since the last access exceeds one minute, the session is considered expired and then a new record with value ``Session expired and restarted`` will be forwarded to downstream.


## Stateful Anomaly Detection with Dynamic Thresholds
This Kafka Stream application aggregated sensor readings for each key and analyzes the aggregated values to detect anomalies. 

The input record for this use case is a key-value pair, with the key being the sensor ID and the value being the current sensor reading.
By calling the following endpoint a sensor reading event will be produced:
```sh
curl POST /v1/api/iot/status 
{
  "key": "1",
  "status": 42.0
}
```
The anomaly detection analysis is scheduled every 20 seconds of wall clock time
If any aggregated value exceeds the threshold then a new record with value ``Anomaly detected`` will be forwarded to downstream.


## Prerequisites
* Java 17 or higher
* Maven
* Docker (optional, for running Docker Compose which include Zookeeper and Apache Kafka)


## Running the Application
1. **Start Kafka and Zookeeper by using Docker Compose file in the repository**:
   ```sh
   docker-compose up
   ```
   
2. **Build**:
   ```sh
   mvn clean package
   ```
   
3. **Run the application**

   Use `sliding` profile to run Stateful Session Management with Custom Expiration Logic.
   Use `session` profile to run Stateful Anomaly Detection with Dynamic Thresholds.
   
   For Unix-like systems
   ```sh
   mvn spring-boot:run -Dspring-boot.run.profiles=sliding
   mvn spring-boot:run -Dspring-boot.run.profiles=session
   ```
   For Windows:
   ```sh
   mvn spring-boot:run "-Dspring-boot.run.profiles=sliding"
   mvn spring-boot:run "-Dspring-boot.run.profiles=session"
   ```
   
4. **calling endpoints**:
   
   Shell script to run the application on Unix-like systems:
   ```sh
   ./scripts/run.sh
   ```
   Batch script to run the application on Windows:
   ```sh
   scripts\run.bat
   ```
