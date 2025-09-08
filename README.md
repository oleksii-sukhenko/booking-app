![logo.png](target/logo.png)

# 🏨 Booking App

Welcome to **Booking App**, a Java-based RESTful web application for managing accommodation listings, user bookings, secure payments, and admin notifications.  
The project follows modern Spring Boot architecture and integrates with third-party services like **Stripe** and **Telegram**.

This is a portfolio-ready application designed to showcase full-stack backend development with real-world use cases.

---

## 🚀 Introduction

**Booking App** simulates a real-world accommodation booking platform. It includes:

- User registration & authentication
- CRUD operations for accommodations
- Booking management with availability checks
- Secure payments via Stripe
- Admin notifications via Telegram bot
- Role-based access control (CUSTOMER / MANAGER)

It provides a comprehensive backend structure using Spring Boot, ideal for mastering REST APIs, Spring Security, JPA, containerization, and third-party API integrations.

---

## 📊 Technologies Used

- Java 21
- Spring Boot 3.2
- Spring Security & JWT
- Spring Data JPA (Hibernate)
- PostgreSQL 14 (via Docker & `.env` configuration)
- Stripe API for payments
- Telegram Bot API for notifications
- MapStruct for DTO mapping
- Docker & Docker Compose
- Swagger / OpenAPI
- Testcontainers for integration testing
- JUnit & Mockito for unit tests
- GitHub Actions for CI/CD

---

## 🚛 Features and Functionalities

### ✅ Authentication & Authorization
- Register as a user
- Login and receive JWT token
- Change password and update profile
- Role-based access (CUSTOMER / MANAGER / ADMIN)

### 🏠 Accommodations
- Managers can:
  - Create new accommodations
  - Update and delete listings
- Customers can:
  - View accommodations
  - Filter by address, amenities

### 📆 Booking Management
- Create bookings for available dates
- View and manage personal bookings
- Admins can manage all bookings
- Prevents overlapping reservations

### 💳 Payments (Stripe)
- Initiate a secure payment session via Stripe
- Handle success and cancel events
- Track payment statuses (PENDING, PAID, CANCELED, FAILED, EXPIRED)

### 📬 Telegram Notifications
- Automatically notify admins of:
  - New bookings
  - Cancellations
  - New accommodations
  - Successful payments

---

## 📁 Project Structure

```
booking-app/
├── src/main/java/com/booking
│ ├── config                # Security and Swagger config
│ ├── controller            # REST controllers
│ ├── dto                   # Request and response DTOs
│ ├── exception             # Global exception handling
│ ├── mapper                # MapStruct interfaces
│ ├── model                 # JPA entities
│ ├── repository            # Spring Data JPA repositories
│ ├── security              # JWT-based security logic
│ ├── service               # Business logic layer
│ ├── stripe                # Stripe integration
│ └── telegram              # Telegram bot integration
├── src/test                # Unit and integration tests
├── Dockerfile
├── docker-compose.yml
├── .env
└── pom.xml
```

---

## 🗄️ Database Schema Overview

![img.png](target/scheme.png)

---

## 📅 How to Run This Project

### 🔧 Prerequisites

- Java 21
- Maven
- Docker
- Stripe account (API key)
- Telegram bot token (optional for notifications)

---

### 🛠 1. Clone the Repository

```bash
git clone https://github.com/oleksii-sukhenko/booking-app.git
cd booking-app
```

---

### 📦 2. Configure Environment

Create a `.env` file:

```env
POSTGRES_DB=booking
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/booking
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
STRIPE_SECRET_KEY=your-stripe-key
JWT_SECRET=your-jwt-secret
TELEGRAM_TOKEN=your-telegram-bot-token
ADMIN_CHAT_ID=your-telegram-chat-id
```

---

### ⚙️ 3. Build the Project

```bash
./mvnw clean install
```

---

### 🐳 4. Run with Docker Compose

```bash
docker-compose up --build
```

---

### 🌐 5. Access the App

- API base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 🌀 API Documentation

Interactive docs are available at:

* Local:
```
http://localhost:8080/swagger-ui/index.html
```

* Production:
```
http://http://ec2-52-90-232-242.compute-1.amazonaws.com/swagger-ui/index.html#/
```

---

## 🧪 Testing

- Integration tests using Testcontainers
- Unit tests with Mockito
- Example: `PaymentServiceImplTest`, `BookingControllerTest`

---

## 🔹 Postman Collection

A Postman collection is available (manual export required).

Includes requests for:

- Auth (Register, Login)
- Accommodations CRUD
- Booking flow
- Payments (Stripe)
- Notifications

---

## 📹 Loom Demo

> Watch the demo video to see how everything works in action: [BookingApp Loom Video Demo](https://www.loom.com/share/e7e6be9d9b7c4fa4bb7879d7ee79b994?sid=78d7c84f-9d64-4c4b-b025-fa8d73763984)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

⭐ **Found this helpful?** Star the repo and connect with me on [LinkedIn](https://www.linkedin.com/in/oleksii-sukhenko/)!