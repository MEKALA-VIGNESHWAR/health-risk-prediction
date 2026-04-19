# 🏥 RealTime Health Risk Prediction System

A full-stack healthcare application that provides **real-time diabetes and heart disease risk predictions** using machine learning, powered by **Spring Boot** and **Supabase PostgreSQL**.

---

## 🏗️ Architecture

```
┌──────────────────────────────────┐
│         Frontend (HTML/JS)       │
│  login · patient · doctor dash   │
└──────────────┬───────────────────┘
               │ REST API
┌──────────────┴───────────────────┐
│     Spring Boot Backend (Java)   │
│  Auth · Predictions · Alerts     │
└──────────────┬───────────────────┘
               │ JDBC + REST
┌──────────────┴───────────────────┐
│     Supabase PostgreSQL          │
│  Users · Predictions · Alerts    │
└──────────────────────────────────┘
```

## 📁 Project Structure

```
RealTime/
├── backend/                  # Spring Boot API
│   ├── src/main/java/        # Java source code
│   │   └── com/example/demo/
│   │       ├── config/       # App, Security, JPA, Supabase config
│   │       ├── controller/   # REST API controllers
│   │       ├── dto/          # Data Transfer Objects
│   │       ├── entity/       # JPA entities
│   │       ├── exception/    # Global exception handler
│   │       ├── repository/   # JPA repositories
│   │       ├── security/     # JWT authentication filter
│   │       ├── service/      # Business logic
│   │       └── util/         # Utility classes
│   ├── src/main/resources/
│   │   └── application.properties  # Config (env-var ready)
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                 # Static HTML/CSS/JS dashboards
│   ├── login.html
│   ├── index.html
│   ├── patient-dashboard.html
│   ├── doctor-dashboard.html
│   ├── enhanced-dashboard.html
│   └── app.html
├── ml/                       # ML model source (diabetes/heart)
│   ├── diabetes/
│   └── heart/
└── data/                     # Training datasets
    ├── diabetes.csv
    └── heart_clean.csv
```

## 🚀 Quick Start

### Prerequisites
- **Java 21** (Eclipse Temurin recommended)
- **Maven 3.8+** (or use the included `mvnw` wrapper)
- **Supabase** account with a PostgreSQL project

### 1. Clone & Configure

```bash
git clone <repo-url>
cd RealTime
```

### 2. Set Environment Variables

The app uses environment variables with sensible defaults. For production, set:

| Variable | Description | Required |
|----------|-------------|----------|
| `DATABASE_URL` | Supabase PostgreSQL JDBC URL | ✅ |
| `DATABASE_USERNAME` | Database username | ✅ |
| `DATABASE_PASSWORD` | Database password | ✅ |
| `SUPABASE_URL` | Supabase project REST URL | ✅ |
| `SUPABASE_ANON_KEY` | Supabase anonymous key | ✅ |
| `SUPABASE_SERVICE_ROLE_KEY` | Supabase service role key | ✅ |
| `PORT` | Server port (default: 8080) | ❌ |
| `LOG_LEVEL` | App log level (default: INFO) | ❌ |

### 3. Build & Run

```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/realtime-app-1.0.0.jar
```

Open **http://localhost:8080** — the frontend is bundled into the JAR.

### 4. Default Test Users

On first startup, the app creates these test accounts:

| Username | Password | Role |
|----------|----------|------|
| `testuser` | `Test@123` | Patient |
| `Chintu_77` | `Chintu@123` | Patient |
| `doctor1` | `Doctor@123` | Doctor |

---

## 🐳 Docker Deployment

```bash
cd backend
docker build -t realtime-app .
docker run -p 8080:8080 \
  -e DATABASE_URL="jdbc:postgresql://..." \
  -e DATABASE_USERNAME="postgres.xxx" \
  -e DATABASE_PASSWORD="your-password" \
  -e SUPABASE_URL="https://xxx.supabase.co" \
  -e SUPABASE_ANON_KEY="your-anon-key" \
  -e SUPABASE_SERVICE_ROLE_KEY="your-service-key" \
  realtime-app
```

## ☁️ Cloud Deployment (Render / Railway / Fly.io)

1. **Set the build command**: `cd backend && ./mvnw clean package -DskipTests`
2. **Set the start command**: `java -jar backend/target/realtime-app-1.0.0.jar`
3. **Add environment variables** (see table above)
4. The app reads `PORT` from env automatically

---

## 🔌 API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/health` | ❌ | Health check |
| POST | `/api/auth/register` | ❌ | Register user |
| POST | `/api/auth/login` | ❌ | Login (returns JWT) |
| POST | `/api/predict/diabetes` | ❌ | Diabetes prediction |
| POST | `/api/predict/heart` | ❌ | Heart disease prediction |
| GET | `/api/dashboard/**` | ❌ | Dashboard data |
| GET | `/api/alerts/**` | ❌ | Patient alerts |
| GET | `/api/doctor/**` | ❌ | Doctor endpoints |

---

## 🗄️ Database

Uses **Supabase PostgreSQL** exclusively. Tables are auto-created on first startup via Hibernate `ddl-auto=update`.

### Tables
- `users` — User accounts (patients & doctors)
- `diabetes_predictions` — Diabetes risk predictions
- `heart_predictions` — Heart disease predictions
- `alerts` — Health risk alerts
- `doctor_notes` — Doctor clinical notes
- `notifications` — User notifications

---

## 📝 License

MIT
