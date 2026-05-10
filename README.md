# Distributed Systems Course AGH UST

This repository contains the code and materials for the Distributed Systems course at AGH University

--- 

## Projects
1. **Sockets - [Chat App](https://github.com/smigass/Distributed-Systems-AGH/tree/master/task1/ChatApp)**
Chat application using sockets written in Java. It allows multiple clients to connect to a server and exchange messages in real-time.
   - Allows users to change protocol between TCP and UDP.
   - Supports UDP multicast.
   - User interface for sending and receiving messages

2. **REST API - [Flight Radar](https://github.com/smigass/Distributed-Systems-AGH/tree/master/task2)**
Flight tracking application that integrates data from multiple external APIs (OpenSky, AviationStack, AeroDataBox).
   - Asynchronous data fetching from multiple REST services.
   - Interactive web interface using Leaflet for live flight tracking.
   - Real-time map monitoring of aircraft and airport departures.

3. **Middleware - [Distributed Systems Middleware](https://github.com/smigass/Distributed-Systems-AGH/tree/master/task3)**
Exploration of different middleware technologies for distributed communication.
   - **[Smart Home](https://github.com/smigass/Distributed-Systems-AGH/tree/master/task3/smart-home)**: Distributed IoT management system built with ZeroC Ice, featuring complex device hierarchies (lights, TVs, weather stations) defined in Slice.
   - **[Web gRPC](https://github.com/smigass/Distributed-Systems-AGH/tree/master/task3/web-grpc)**: Modern web communication using ConnectRPC and gRPC-Web for seamless integration between a Vite/React frontend and a Node.js backend.

4. **Asynchronous Messaging - [RabbitMQ System](https://github.com/smigass/Distributed-Systems-AGH/tree/master/task4/rabbitmq-system)**
Distributed system for managing space missions and logistics between space agencies and carriers, built on RabbitMQ.
   - Implementation of request-response and publish-subscribe patterns.
   - Intelligent message routing based on topics and service types.
   - Administrative module for system monitoring and global message broadcasting.
  

