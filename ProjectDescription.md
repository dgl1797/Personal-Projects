# Application Architecture:

- frontend (Angular) with TailwindCSS and DaisyUI
- 2 o 3 instances of Python Server for handling messages persisted in MongoDB.
- 2 o 3 instances of Java Backend with Spring Boot for handling base interactions with frontend, CRUD operations and interfacing with MySQL database using Java Persistency APIs and Hibernate with Redis as Session support, illegal actions percistency and validation helper.
- 1 Load Balancer (Nginx) for python nodes
- 1 MySQL node
- 1 MongoDB node

# Network Architecture:

- All Java related stack (MySQL; Redis) has to be isolated from the Python execution stack. For that purpose there needs to be a Java subnetwork with Java, MySQL, Redis and a Python subnetwork with MongoDB, Python, Nginx

# CI/CD:

Use Git Actions to Continuously Integrate/Test[/Deploy] the application (deployment should be done only if a free deployment method is found, try for instance Radius open-cloud).

# Description:

An application where you can initialize projects and store various tasks in it, labelling as "todo"; "ongoing"; "finished". Tasks marked as "todo" will be selectable for assigning people to them and they will then automatically be marked as "ongoing" then the assignees will be able to interact with it by clicking "complete" that will mark them as finished. The app must have a navbar on top and a sidebar with users to be able to open chats. Standard users cannot start projects, it will be only for premium user.

# Learning Objectives:

- Deployment of a distributed stack with load balancers and multiple servers
- Angular Framework for frontend development
- Integration of tools to build a strong and reliable stack
- Spring Framework with JPA and Hibernate for data sources handling and ChronJobs. Endpoints Testing and Validation.
- Docker advanced development environment setup with networking and VIP/DNSRR deploy modalities with replicas.
- MySQL Design and usage
- MongoDB Design and usage
- Redis Design and usage for session management
- CI/CD using GitActions
- Deoployment in the cloud using Kubernetes and Radius

# Used Stack:

## Frontend

- Angular
- TailwindCSS with DaisyUI; PostCSS; AutoPrefixer

## Backend - Java

- Spring Boot with JPA and Hibernate over MySQL
- JWT session tokens with Redis
- MySQL design and implementation from scratch and integrated with JPA

## Backend - Python

- SocketIO Server
- MongoDB design and implementation from scratch
- Redis as coordinator of Socket connections
- Loadbalancing middleware with Nginx

## Deployment

- Kubernetes setup for cloud deployment
- CI/CD using GitActions and Jenkins

## MySQL Design:

![Image not found](./docs/ER.png)

# Test Data

- username: a.desica
- email: andrea.desica@gmail.com
- passw: P@ssw0rd!
