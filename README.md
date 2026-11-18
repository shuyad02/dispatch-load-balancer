# FreightFox - Dispatch Load Balancer

A production-ready Spring Boot backend service designed to optimize delivery order allocation across a fleet of vehicles based on geographical coordinates, vehicle capacity limits, and priority scheduling.

---

## 1. Key Features

- **Haversine Distance Engine**: Accurate great-circle distance calculation between geographical coordinates in kilometers.
- **Priority-Driven Allocation**: Enforces strict scheduling hierarchy (`HIGH` -> `MEDIUM` -> `LOW`).
- **Capacity & Load Balancing**: Strict enforcement of vehicle capacity constraints, assigning orders to vehicles that minimize incremental travel distance.
- **Pluggable Architecture**: Uses the **Strategy Design Pattern** (`DispatchOptimizationStrategy`) allowing easy extension of optimization algorithms.
- **Database Upsert Support**: Persists order and fleet data in H2 in-memory database with seamless updates.
- **OpenAPI 3 / Swagger UI**: Interactive API documentation at `/swagger-ui.html`.
- **Comprehensive Test Suite**: Unit and integration test coverage for algorithmic correctness and boundary edge cases.
- **Production-Ready Deployment**: Containerized with multi-stage `Dockerfile`, pre-configured for platforms like Render and Railway.

---

## 2. API Specifications

### Base URL: `http://localhost:8080` (or live deployed host)

### 1. Register Delivery Orders
- **Endpoint**: `POST /api/dispatch/orders`
- **Request Body**:
  ```json
  {
    "orders": [
      {
        "orderId": "ORD001",
        "latitude": 28.6139,
        "longitude": 77.2090,
        "address": "Connaught Place, Delhi, India",
        "packageWeight": 15,
        "priority": "HIGH"
      }
    ]
  }
  ```
- **Response** (`200 OK`):
  ```json
  {
    "message": "Delivery orders accepted.",
    "status": "success"
  }
  ```

### 2. Register Fleet Vehicles
- **Endpoint**: `POST /api/dispatch/vehicles`
- **Request Body**:
  ```json
  {
    "vehicles": [
      {
        "vehicleId": "VEH001",
        "capacity": 100,
        "currentLatitude": 28.7041,
        "currentLongitude": 77.1025,
        "currentAddress": "Karol Bagh, Delhi, India"
      }
    ]
  }
  ```
- **Response** (`200 OK`):
  ```json
  {
    "message": "Vehicle details accepted.",
    "status": "success"
  }
  ```

### 3. Retrieve Dispatch Plan
- **Endpoint**: `GET /api/dispatch/plan`
- **Response** (`200 OK`):
  ```json
  {
    "dispatchPlan": [
      {
        "vehicleId": "VEH001",
        "totalLoad": 15,
        "totalDistance": "12 km",
        "assignedOrders": [
          {
            "orderId": "ORD001",
            "latitude": 28.6139,
            "longitude": 77.2090,
            "address": "Connaught Place, Delhi, India",
            "packageWeight": 15.0,
            "priority": "HIGH"
          }
        ]
      }
    ]
  }
  ```

### 4. Interactive Swagger UI
- Local: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- API Docs JSON: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## 3. How to Run Locally

### Prerequisites
- JDK 21
- Maven 3.9+ (or `./mvnw`)

### Build and Run
```bash
# Run tests
mvn test

# Package JAR
mvn clean package

# Run application
java -jar target/dispatch-load-balancer-1.0.0.jar
```

---

## 4. Deployment Guide (Free on Render)

1. Push this project to your GitHub repository.
2. Sign in to [Render.com](https://render.com) and create a **New Web Service**.
3. Select your GitHub repository.
4. Set:
   - **Environment**: `Docker`
   - **Branch**: `main`
5. Click **Create Web Service**. Render builds the `Dockerfile` automatically and provides a live HTTPS URL.

---

## 5. Loom Video Walkthrough Script (3-4 mins)

1. **Introduction (30s)**: Introduce yourself, mention the FreightFox assignment, and state the problem objective: vehicle load balancing with capacity, priority, and distance optimization.
2. **Architecture & Design (45s)**: Briefly highlight the layered Spring Boot architecture, Strategy pattern (`DispatchOptimizationStrategy`), and Haversine formula calculation.
3. **Live Demonstration (1.5m)**:
   - Open Swagger UI or Postman.
   - Send vehicle input (`POST /api/dispatch/vehicles`).
   - Send order batch (`POST /api/dispatch/orders`).
   - Execute `GET /api/dispatch/plan` and show the allocated vehicles, total load, and route distance.
4. **Validation & Edge Cases (45s)**: Demonstrate edge cases like overcapacity handling or priority adherence (`HIGH` orders assigned before `LOW`).
5. **Conclusion (15s)**: Summarize and thank the FreightFox team.
