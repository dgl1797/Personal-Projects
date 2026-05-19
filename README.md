# NJTodo — Not Just a Todo App

> A full-stack distributed project management and team collaboration application with real-time messaging, built as a personal learning project for exploring modern distributed systems architecture.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Requirements](#requirements)
3. [Application Features & Functionalities](#application-features--functionalities)
4. [System Architecture](#system-architecture)
5. [Tech Stack](#tech-stack)
6. [Theme & Styling](#theme--styling)
7. [Network Topology](#network-topology)
8. [API Endpoints](#api-endpoints)
9. [Database Design](#database-design)
10. [Learning Objectives](#learning-objectives)

---

## Project Overview

**NJTodo** is a personal-learning distributed web application that combines project management with real-time team collaboration. Users can create and manage projects with tasks organized in a Kanban-like workflow (todo → ongoing → finished), while simultaneously communicating with team members through an integrated live chat system. The application is designed to run in a Docker-based multi-service environment with high availability, load balancing, and service isolation.

---

## Requirements

### Functional Requirements

| ID | Requirement | Status |
|---|---|---|
| FR-01 | Users must be able to register with username, email, password, and subscription plan | ✅ Implemented |
| FR-02 | Users must be able to log in and receive a JWT session token | ✅ Implemented |
| FR-03 | Users must be able to log out and invalidate their session | ✅ Implemented |
| FR-04 | Users must be categorized as **Standard** or **Premium** | ✅ Implemented |
| FR-05 | Only Premium users can create projects | ✅ Implemented |
| FR-06 | Any user can be added as a participant to a project | ✅ Implemented |
| FR-07 | Projects must support CRUD operations | ✅ Implemented |
| FR-08 | Tasks within projects must have states: `todo`, `ongoing`, `finished` | ✅ Implemented |
| FR-09| Tasks marked as `todo` must be selectable for user assignment | ✅ Implemented |
| FR-10 | Assigning a user to a task automatically transitions it to `ongoing` | ✅ Implemented |
| FR-11 | Users can mark tasks as `finished` | ✅ Implemented |
| FR-12 | Tasks must support CRUD operations | ✅ Implemented |
| FR-13 | Real-time 1-on-1 chat must be available between users | ✅ Implemented |
| FR-14 | Chat must persist message history | ✅ Implemented |
| FR-15 | Users must see who is currently online | ✅ Implemented |
| FR-16 | Notifications must appear for new messages when chat is not focused | ✅ Implemented |
| FR-17 | The application must have a top navbar and a sidebar/drawer for chat | ✅ Implemented |
| FR-18 | Session tokens must expire and be validated on each request | ✅ Implemented |
| FR-19 | Password must be hashed with salt before storage | ✅ Implemented |

### Non-Functional Requirements

| ID | Requirement | Status |
|---|---|---|
| NFR-01 | Java and Python services must run in isolated Docker networks | ✅ Implemented |
| NFR-02 | Multiple replicas of Java and Python servers must be supported | ✅ Implemented |
| NFR-03 | Python chat servers must use DNS Round-Robin load balancing via Nginx | ✅ Implemented |
| NFR-04 | Java servers must use VIP (Virtual IP) endpoint mode for session consistency | ✅ Implemented |
| NFR-05 | Redis must coordinate chat state across multiple Python server replicas | ✅ Implemented |
| NFR-06 | Redis must manage JWT session tokens for the Java backend | ✅ Implemented |
| NFR-07 | Frontend must be responsive (mobile + desktop) | ✅ Implemented |
| NFR-08 | CORS filters must protect API endpoints | ✅ Implemented |
| NFR-09 | Auth filter middleware must protect authenticated endpoints | ✅ Implemented |
| NFR-10 | CI/CD pipeline via GitHub Actions must be configured | ⬜ Planned |

---

## Application Features & Functionalities

### 1. User Authentication & Subscription Tiers

- **Sign Up**: Users register with username, email, password, and choose between **Standard** or **Premium** subscription.
- **Log In**: Authenticated users receive a JWT token stored in sessionStorage.
- **Log Out**: Token is invalidated on the server and removed from client storage.
- **Session Management**: JWT tokens validated on every API request via a middleware filter. Sessions maintained in Redis with configurable expiration.

### 2. Project Management (Premium Feature)

- **Create Project**: Only Premium users can create new projects.
- **Read Project**: View full project details including participants and tasks.
- **Update Project**: Add or remove participants from a project.
- **Delete Project**: Project owner can delete a project.
- **Access Control**: Participants-based access control — only project members can view/modify a project.

### 3. Task Management (Kanban Workflow)

Tasks follow a lifecycle:

```
  ┌─────────┐    assign user    ┌──────────┐    mark complete    ┌──────────┐
  │  TODO   │ ────────────────→ │ ONGOING  │ ──────────────────→ │ FINISHED │
  └─────────┘                   └──────────┘                     └──────────┘
```

- **Create Task**: Add tasks to a project with name, optional description, and initial state.
- **Assign Task**: Only `todo` tasks can be assigned to users. Assignment moves the task to `ongoing`.
- **Complete Task**: Assigned users can mark tasks as `finished`.
- **Update Task**: Modify task state, description, or assignees.
- **Delete Task**: Remove a task from a project.
- **View Task**: Retrieve full task details including assignees and project info.

### 4. Real-Time Chat System

- **WebSocket Connection**: Establishes persistent bidirectional communication via Python WebSocket servers.
- **Online User List**: Real-time view of currently connected users.
- **Direct Messaging**: 1-on-1 chat between any two online users.
- **Message History**: Persisted in MongoDB and retrievable when opening a chat.
- **Notifications**: Visual badge and dropdown notifications when receiving messages from non-focused chats.
- **Sound Alert**: Audio notification played on new incoming messages.
- **Multi-Tab Chat**: Users can open multiple chats simultaneously in the sidebar drawer.
- **Chat Sidebar/Drawer**: Slides in from the right, showing open chat tabs and message threads.

### 5. Dashboard & Navigation

- **Top Navbar**: Application icon, project creation button (Premium) / upgrade button (Standard), notification bell, user profile dropdown (Profile, Projects, Tasks, Logout).
- **Chat Drawer**: Fixed bottom-right button opens a full sidebar drawer for chat management.
- **Home Page**: Left sidebar showing online users list, main area with user profile overview and incomplete tasks.

---

## System Architecture

The application follows a distributed microservices architecture orchestrated via Docker Compose.

```
                           ┌──────────────────────┐
                    ┌─────│   Angular Frontend     │◄───── HTTP (port 5000)
                    │     │   (Tailwind + DaisyUI)  │
                    │     └──────────┬──────────────┘
                    │                │
                    │     ┌──────────┴──────────┐
                    │     │     HTTP/WS Router    │
                    │     └──────┬─────────┬──────┘
                    │            │         │
              ┌─────┴─────┐     │    ┌────┴──────────┐
              │ Javanet   │     │    │   Pynet       │
              │ (VIP)     │     │    │   (DNSRR)     │
              └─────┬─────┘     │    └───────┬───────┘
                    │           │            │
              ┌─────┴─────┐    │     ┌──────┴───────┐
              │  Nginx    │    │     │   Nginx LB    │
              │  (none)   │    │     │  (port 5002)  │
              └─────┬─────┘    │     └──────┬───────┘
                    │           │            │
              ┌─────┴─────┐    │     ┌──────┴────────┐
              │  Java     │    │     │ Python Chat    │
              │  Replicas │    │     │ Server Replicas│
              └─────┬─────┘    │     └──────┬─────────┘
                    │           │            │
              ┌─────┴─────┐    │     ┌──────┴──────────┐
              │  MySQL    │    │     │  MongoDB         │
              └───────────┘    │     └──────────────────┘
                    │           │            │
              ┌─────┴─────┐    │     ┌──────┴──────────┐
              │  Redis    │    │     │  Redis           │
              │(Sessions) │    │     │(Chat State)      │
              └───────────┘    │     └──────────────────┘
                               │
                    ┌──────────┴──────────┐
                    │   Docker Networks   │
                    │ 192.168.12.0/24 (javanet) │
                    │ 192.168.13.0/24 (pynet)   │
                    └───────────────────────────┘
```

### Service Isolation

The stack is split into two isolated Docker bridge networks:

- **javanet** (`192.168.12.0/24`): Java backend, MySQL, Redis (Session Manager)
- **pynet** (`192.168.13.0/24`): Python chat servers, Nginx balancer, MongoDB, Redis (Chat State)

The frontend has network access to both.

---

## Tech Stack

### Frontend
| Technology | Purpose |
|---|---|
| **Angular** (Standalone Components) | SPA framework with routing |
| **TypeScript** | Type-safe development |
| **TailwindCSS + DaisyUI** | Utility-first CSS framework with component library |
| **PostCSS + AutoPrefixer** | CSS post-processing |
| **RxJS** | Reactive state management for real-time data |
| **Angular Reactive Forms** | Form validation and submission |
| **WebSocket API** | Bidirectional real-time communication |

### Backend — Java (`javanet`)
| Technology | Purpose |
|---|---|
| **Spring Boot** (v3.x) | REST API framework |
| **Jakarta RESTful Web Services (JAX-RS)** | Endpoint annotations |
| **JPA + Hibernate** | ORM for MySQL interaction |
| **Spring Data JPA Repositories** | Database query abstraction |
| **Redis** (Spring Data Redis) | Session token storage and validation |
| **JWT** | Authentication token generation and parsing |
| **Jakarta Validation** | Input payload validation |
| **Maven** | Build and dependency management |

### Backend — Python (`pynet`)
| Technology | Purpose |
|---|---|
| **Python 3.13** | Runtime |
| **WebSockets** (asyncio-based) | Real-time chat protocol |
| **PyMongo** (Motor) | Async MongoDB driver |
| **Redis** (aioredis / redis-py) | Chat state orchestration |
| **asyncio** | Async event loop for concurrent connections |

### Data Stores
| Technology | Purpose | Network |
|---|---|---|
| **MySQL 8.0** | User data, projects, tasks (structured/relational data) | javanet |
| **MongoDB 7.0** | Chat messages, notifications (document-based) | pynet |
| **Redis 7.2** (×2) | Session management (javanet) + Chat state orchestration (pynet) | both |

### Infrastructure
| Technology | Purpose |
|---|---|
| **Docker Compose** | Multi-service orchestration with custom networks |
| **Nginx** | Reverse proxy and load balancer for Python chat replicas |
| **Docker VIP** | Virtual IP endpoint mode for Java replicas (sticky sessions) |
| **Docker DNSRR** | DNS Round-Robin for Python replicas (stateless balancing) |
| **GitHub Actions** | CI/CD pipeline (planned) |

---

## Theme & Styling

The application uses a professional dark-themed UI with a cohesive color palette designed for readability and modern aesthetics.

### Color Palette

| Role | Color | Hex Code | Usage |
|---|---|---|---|
| **Primary** | Sky Blue | `#0369a1` (sky-700) | Navbar, buttons, headers |
| **Secondary** | Pink | `#be185d` (pink-700) | Call-to-action buttons, accents |
| **Surface** | Slate Gray | `#475569` (slate-600) | Cards, panels, chat backgrounds |
| **Background** | Off-white/light gray | `base-200` (DaisyUI) | Page backgrounds |
| **Chat Sent** | Primary Blue | `chat-bubble-primary` | Sent messages |
| **Chat Received** | Light Slate | `#64748b` (slate-400) | Received messages |

### Design Elements

- **Navbar**: Fixed top bar with sky-blue background, shadow, containing logo, action buttons, notification bell, and user avatar menu.
- **Cards & Panels**: Rounded corners (`rounded-md`), shadow effects, slate backgrounds.
- **Chat Interface**: Bottom input bar with send button, message bubbles styled with DaisyUI `chat` components.
- **Chat Drawer**: Overlay sidebar with animated transition, contains tabbed chat channels and message threads.
- **Notification Badge**: Red indicator badge on the bell icon showing unread message count.
- **Loading States**: DaisyUI loading spinners (`loading-infinity`, `loading-ring`) in pink.
- **Typography**: Clean sans-serif (default Tailwind), white text on colored surfaces.

### Responsive Behavior

- **Mobile**: Single column layout, hidden sidebar, stacked elements, hamburger/drawer navigation.
- **Tablet**: Partial sidebar visible, adjusted grid layouts.
- **Desktop**: Full sidebar, multi-column grid with chat drawer overlay.

### Component Highlights

| Component | Description |
|---|---|
| **Login/Signup Page** | Split layout (desktop) with app icon, description text, and form card |
| **Dashboard** | Navbar + router outlet for sub-pages + floating chat button |
| **Home Page** | Online users sidebar + user info panel + incomplete tasks panel |
| **Projects Page** | Placeholder for project list/management |
| **Alert Component** | Reusable toast notification (info, error, warning, success) |

---

## Network Topology

### Docker Networks

```
┌─────────────────────────────────────────────────┐
│                   docker-compose                  │
│                                                   │
│  ┌──────────── javanet ────────────┐              │
│  │  192.168.12.0/24               │              │
│  │  ┌─────────┐ ┌────────┐ ┌───┐  │              │
│  │  │ MySQL   │ │ Redis  │ │Java│  │              │
│  │  │ 8.0     │ │7.2(Ses)│ │Srv│  │              │
│  │  └─────────┘ └────────┘ └───┘  │              │
│  └────────────────────────────────┘              │
│                                                   │
│  ┌───────────── pynet ────────────┐              │
│  │  192.168.13.0/24               │              │
│  │  ┌────────┐ ┌──────┐ ┌──────┐  │              │
│  │  │MongoDB │ │Redis │ │Python│  │              │
│  │  │ 7.0    │ │7.2   │ │Chat  │  │              │
│  │  └────────┘ │(State│ │Srv   │  │              │
│  │             └──────┘ └──────┘  │              │
│  │  ┌─────────────────────┐      │              │
│  │  │ Nginx LB (port 5002)│      │              │
│  │  └─────────────────────┘      │              │
│  └────────────────────────────────┘              │
│                                                   │
│  ┌───── frontend ──────────────────────────────┐ │
│  │  Angular SPA (port 5000) — connects to both  │ │
│  └──────────────────────────────────────────────┘ │
└───────────────────────────────────────────────────┘
```

### Endpoint Modes

| Service | Mode | Behavior |
|---|---|---|
| **Java Replicas** | **VIP** (Virtual IP) | Docker assigns a virtual IP to the service. Client connects to the VIP, and traffic is routed to a single replica for the duration of the session. Ensures sticky sessions for stateful interactions (authenticated user sessions). |
| **Python Replicas** | **DNSRR** (DNS Round-Robin) | Docker DNS resolves the service name to a list of replica IPs. Each new connection gets a random replica IP, distributing load. Stateless design allows any replica to handle any client (chat state is coordinated via Redis). |

### Load Balancing

- **Java Backend**: No external load balancer needed — Docker VIP handles transparent session affinity.
- **Python Chat**: Nginx reverse proxy on port `5002` distributes incoming WebSocket connections across Python replicas using round-robin.

### Fault Tolerance (Chat Service)

Python chat servers implement a heartbeat-based failure detection:
1. Each server periodically notifies Redis of its presence (key with 60s TTL).
2. Servers maintain a routing table of users connected to other servers.
3. If a server fails (heartbeat stops), Redis key expires, remaining servers detect the failure and reassign orphaned users.
4. Messages are forwarded through inter-server WebSocket connections.

---

## API Endpoints

### Java Backend (`http://localhost:5001/apis`)

#### Authentication

| Method | Path | Auth Required | Description |
|---|---|---|---|
| `GET` | `/auth` | No (Basic) | Login: Authenticate with `Authorization: username:password` header, returns JWT token. |
| `POST` | `/auth` | No | Register: Create new user account. Body: `{ userInfo: { username, email, type }, password }`. Returns JWT token. |
| `DELETE` | `/auth/{username}` | Yes (JWT Bearer) | Logout: Invalidate session token. |

#### Users

| Method | Path | Auth Required | Description |
|---|---|---|---|
| `GET` | `/users/{username}` | Yes (JWT Bearer) | Get user dashboard data: owned projects, participations, incomplete tasks, user info. |

#### Projects

| Method | Path | Auth Required | Description |
|---|---|---|---|
| `POST` | `/projects` | Yes (JWT Bearer) | Create project (Premium only). Body: `{ projectName, participants: [{ username, role }] }`. |
| `GET` | `/projects/{projectId}` | Yes (JWT Bearer) | Get full project details with participants and tasks. |
| `PUT` | `/projects/{projectId}` | Yes (JWT Bearer) | Add participants to project. Body: `{ newParticipants: [{ username, role }] }`. |
| `DELETE` | `/projects/{projectId}` | Yes (JWT Bearer) | Delete project (owner only). |
| `DELETE` | `/projects/{projectId}/users/{userId}` | Yes (JWT Bearer) | Remove user from project. |

#### Tasks

| Method | Path | Auth Required | Description |
|---|---|---|---|
| `POST` | `/projects/{projectId}/tasks` | Yes (JWT Bearer) | Add task to project. Body: `{ name, description?, state, assignees? }`. |
| `GET` | `/projects/{projectId}/tasks/{taskId}` | Yes (JWT Bearer) | Get full task details. |
| `PUT` | `/projects/{projectId}/tasks/{taskId}` | Yes (JWT Bearer) | Update task (state, description, assignees). |
| `DELETE` | `/projects/{projectId}/tasks/{taskId}` | Yes (JWT Bearer) | Delete task from project. |

### Python Chat Server (`ws://localhost:5002`)

#### WebSocket Endpoints

| Path | Description |
|---|---|
| `/users/{username}` | Client-to-server connection. Handles messaging, chat history retrieval, online list requests. |
| `/servers/{server_ip}` | Server-to-server connection. Handles inter-replica user state synchronization and message forwarding. |

#### WebSocket Message Protocol

All messages are JSON-encoded with a `type` field:

| Type | Direction | Description |
|---|---|---|
| `get:chat` | Client → Server | Request chat history with a target user. `{ target: "username" }` |
| `chat:messages` | Server → Client | Response with full chat history. `{ payload: { target, messages: [...] } }` |
| `get:online` | Client → Server | Request list of currently online users. |
| `online:list` | Server → Client | Response with online users list. `{ payload: ["user1", "user2", ...] }` |
| `send` | Client → Server | Send a message. `{ receiver, content, timestamp }` |
| `receive` | Server → Client | Forward a received message. `{ payload: { sender, receiver, content, timestamp } }` |
| `user:state` | Both | Broadcast user online/offline state. `{ payload: "username", online: true/false }` |

---

## Database Design

### MySQL Schema (javanet — Structured Data)

The Java backend uses JPA/Hibernate ORM to interact with the MySQL database. The schema follows the Entity-Relationship diagram below:
![diagram_image](./docs/ER.png)

**Key Design Decisions:**
- **User** entity has a ManyToMany self-relationship through `UserParticipateProject` (join table with extra columns like `role`, `added_time`).
- **Task** uses a `StateEnum` (`TODO`, `ONGOING`, `FINISHED`) for workflow state management.
- **Project** has a unique constraint on `(owner, name)` to prevent duplicate projects per owner.
- **Task** has a unique constraint on `(pjid, name)` to prevent duplicate task names within a project.
- **Passwords** are hashed with a randomly generated salt using `AccessManager.encrypt()`.
- **JWT tokens** are stored in Redis with TTL for session management.

### MongoDB Schema (pynet — Document Data)

Collection: `messages`

```json
{
  "_id": "ObjectId",
  "sender": "username",
  "receiver": "username",
  "content": "message text",
  "timestamp": 1712345678901
}
```

Indexes:
- `{ sender: 1, receiver: 1 }` — for efficient chat history retrieval
- `{ timestamp: -1 }` — for chronological sorting

### Redis Data Structures

#### Session Manager (javanet)

| Key Pattern | Type | TTL | Value |
|---|---|---|---|
| `session:{token}` | String | Configurable (default: session length) | Username associated with the token |

#### Chat State (pynet)

| Key Pattern | Type | TTL | Value |
|---|---|---|---|
| `server:{ip}` | String | 60 seconds (renewed every 59s) | Online server heartbeat |
| `assigned:{username}` | String | — | Server IP where user is connected |

---

## Learning Objectives

This project was designed to achieve the following learning outcomes:

### Distributed Systems & Networking
- [x] Deployment of a distributed multi-service stack with load balancers and multiple server replicas.
- [x] Docker advanced development environment setup with custom bridge networks, IPAM configuration, and service isolation.
- [x] Comparison and implementation of VIP (Virtual IP) vs DNSRR (DNS Round-Robin) endpoint modes for different service requirements.
- [x] Inter-service communication patterns (direct WebSocket, Redis pub/sub, HTTP REST).

### Frontend Development (Angular)
- [x] SPA development with Angular standalone components and routing.
- [x] Reactive state management with RxJS Subjects and Observables for real-time data flows.
- [x] TypeScript DTOs and service layers for structured API interaction.
- [x] Reactive forms with custom validators (email format, password confirmation).
- [x] WebSocket client integration for real-time bidirectional communication.
- [x] Session/caching management via sessionStorage with service abstraction.

### Backend Development (Java — Spring)
- [x] Spring Boot REST API with JAX-RS endpoint annotations.
- [x] JPA/Hibernate ORM integration with MySQL — entity mapping, relationships, repositories, JPQL queries.
- [x] JWT token generation, parsing, and validation.
- [x] Redis integration for session management with TTL-based expiration.
- [x] Middleware filter chain for authentication (`AuthFilter`) and CORS (`CorsFilter`).
- [x] Input validation with Jakarta Validation annotations and custom validators.
- [x] Password hashing with salt for secure credential storage.
- [x] Service layer pattern for business logic separation.

### Backend Development (Python — WebSockets)
- [x] Asynchronous WebSocket server with Python's `asyncio` and `websockets` library.
- [x] MongoDB integration for document-based message persistence.
- [x] Redis integration for distributed state orchestration (server heartbeat, user routing).
- [x] Fault tolerance: automatic detection of server failures and user reassignment.
- [x] Inter-server communication for message forwarding across replicas.

### Database Design & Usage
- [x] MySQL relational schema design with proper normalization, foreign keys, and constraints.
- [x] MongoDB document schema design for unstructured/chat data.
- [x] Redis in-memory data structures for real-time state coordination.
- [x] Understanding when to use SQL vs NoSQL vs In-Memory databases for different concerns.

### Containerization & Orchestration
- [x] Docker Compose configuration for multi-service orchestration.
- [x] Custom Docker networks with IPAM for service isolation.
- [x] Volume mounting for live development and persistent data.
- [x] Container entrypoints and environment variable configuration.

### CI/CD (Planned)
- [ ] GitHub Actions pipeline configuration for continuous integration and automated testing.
- [ ] Cloud deployment strategy exploration (Radius open-cloud, Kubernetes).

---
