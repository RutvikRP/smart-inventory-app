# 📦 Smart Inventory App

A modern, scalable **Inventory Management System** built with **Spring Boot, PostgreSQL, and Docker**.  
This project demonstrates **clean architecture, best practices, and production-ready deployment**.

---

## 🚀 Features

- 📊 Manage products, stock, and suppliers
- 🌐 RESTful API using Spring Boot
- 🐘 PostgreSQL database with Docker support
- 🏗️ Clean folder structure with DTOs, Services, Repositories
- 🔧 Configurable environments (`dev`, `prod`)

---

## 🛠️ Tech Stack

- **Backend:** Spring Boot (Java)
- **Database:** PostgreSQL (Dockerized)
- **Build Tool:** Maven
- **Deployment:** Docker & Docker Compose

---

## 📂 Project Structure

smart-inventory-app/
├── backend/
│ └── inventory-service/ # Spring Boot service
│
├── frontend/ # (to be added later - Angular/React)
│
└── docker/ # Docker configs

---

## ⚙️ How to Run (Development)

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/smart-inventory-app.git
   cd smart-inventory-app
   ```

docker-compose up -d

cd backend/inventory-service
mvn spring-boot:run

👨‍💻 Author

Rutvik Patel – Aspiring System Engineer
Learning Java Spring Boot + Angular to build scalable, production-grade applications.
