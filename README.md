# 🏥 RealTime Diabetes Prediction App

A full-stack web application for diabetes risk prediction using machine learning, built with **Spring Boot 3.2**, **Java 21**, and a responsive **HTML/CSS/JavaScript** frontend.

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Setup & Installation](#setup--installation)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [User Authentication](#user-authentication)
- [Making Predictions](#making-predictions)
- [Database](#database)
- [Troubleshooting](#troubleshooting)

---

## ✨ Features

### 🔐 Authentication & Security
- User registration with form validation
- JWT-based login/logout system
- Password hashing with BCrypt
- Secure token-based API access
- CORS enabled for frontend communication

### 🎯 Prediction Engine
- **Confidence Level** - Shows prediction certainty (0-100%)
- **Model Information** - Displays algorithm used (Random Forest Decision Model v1.0)
- **Probability Analysis** - Shows risk probabilities for both outcomes
- **Risk Assessment** - HIGH/LOW risk classification
- **Input Validation** - Real-time form validation

### 📊 Dashboard
- User profile information
- Prediction history
- High-risk predictions alert
- Statistics (total predictions, high-risk count)
- Responsive mobile-friendly design

### 🎨 UI/UX
- Modern gradient design with Tailwind CSS
- Dark mode theme
- Smooth animations and transitions
- Real-time alerts and feedback
- Intuitive navigation

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.4
- **Language**: Java 21
- **Database**: H2 (in-memory, configurable to PostgreSQL)
- **Security**: Spring Security + JWT
- **Build Tool**: Maven 3.9.6
- **ORM**: Hibernate/JPA

### Frontend
- **HTML5** + **CSS3** (Tailwind CSS)
- **JavaScript** (ES6+, vanilla, no frameworks)
- **LocalStorage** for token/user data
- **Responsive Design** for all devices

### Database
- **Primary**: H2 (development)
- **Production-Ready**: PostgreSQL via Supabase

---

## 📁 Project Structure

```
RealTime/
├── backend/                       # Spring Boot application
│   ├── src/main/java/com/example/demo/
│   │   ├── config/               # Security, Supabase config
│   │   ├── controller/           # REST endpoints
│   │   ├── dto/                  # Data Transfer Objects
│   │   ├── entity/               # Database entities
│   │   ├── repository/           # JPA repositories
│   │   ├── service/              # Business logic
│   │   └── Application.java      # Entry point
│   ├── src/main/resources/
│   │   └── application.properties # Configuration
│   └── pom.xml                   # Maven dependencies
│
├── frontend/                      # Web UI
│   ├── index.html                # Main application
│   ├── css/                      # Stylesheets
│   ├── js/                       # JavaScript modules
│   └── public/                   # Static assets
│
├── data/                         # Datasets
│   └── diabetes.csv              # Reference data
│
└── ml/                           # ML scripts
    └── diabetes/
```

---

## 🚀 Setup & Installation

### Prerequisites
- **Java 21** or higher
- **Maven 3.9.6** or higher

### Backend Setup

```bash
cd backend
mvn clean package -DskipTests
```

### Frontend Setup

No installation needed - uses vanilla JavaScript. Open `frontend/index.html` directly in browser.

---

## 🏃 Running the Application

### Start Backend
```bash
cd backend
java -jar target/realtime-app-1.0.0.jar
```
Backend runs on: **http://localhost:8080**

### Access Frontend

**Option 1:** Direct File
```
file:///c:/Users/mekal/.vscode/projectjava/RealTime/frontend/index.html
```

**Option 2:** Python Server
```bash
cd frontend
python -m http.server 8000
```

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login user |
| GET | `/api/auth/verify` | Verify token |

### Predictions
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/predict/diabetes` | Make prediction |
| GET | `/api/predict/history/user/{userId}` | Get history |
| GET | `/api/predict/high-risk` | Get high-risk predictions |

### Health
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Health status |

---

## 🔐 Authentication

### Register
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

### Login
```json
{
  "username": "john_doe",
  "password": "SecurePass123!"
}
```

Token automatically saved to `localStorage` as `token`

---

## 🎯 Making Predictions

### Request
```json
{
  "pregnancies": 6,
  "glucose": 148,
  "bloodPressure": 72,
  "skinThickness": 35,
  "insulin": 0,
  "bmi": 33.6,
  "diabetesPedigreeFunction": 0.627,
  "age": 50,
  "userId": "user-uuid"
}
```

### Response
```json
{
  "predictionId": "pred-uuid",
  "prediction": 1,
  "probabilityNoDiabetes": 0.32,
  "probabilityDiabetes": 0.68,
  "message": "Diabetes Positive",
  "risk": "HIGH",
  "confidenceLevel": 0.68,
  "modelUsed": "Random Forest Decision Model v1.0"
}
```

---

## 💾 Database

### Default: H2 In-Memory
```properties
spring.datasource.url=jdbc:h2:mem:realtime
spring.jpa.hibernate.ddl-auto=update
```
✅ Persistent across restarts  
✅ No setup needed

### Switch to PostgreSQL
Update `application.properties` to use Supabase PostgreSQL credentials.

---

## 🔧 Troubleshooting

### Port 8080 Already in Use
```bash
taskkill /F /IM java.exe
```

### Login Says Invalid Credentials
- Verify you registered first
- Check backend is running: `http://localhost:8080/api/health`
- Check browser console (F12) for errors

### Frontend Can't Connect to Backend
- Ensure backend running on port 8080
- Check browser console for CORS errors
- Verify API_BASE_URL in index.html

### Data Lost After Restart
**Old (data lost):**
```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

**New (data persists):**
```properties
spring.jpa.hibernate.ddl-auto=update
```

---

## 📝 Key Features

✅ **Secure Authentication** - JWT tokens, BCrypt hashing  
✅ **Real-time Predictions** - Instant risk assessment  
✅ **Confidence Metrics** - Shows model certainty  
✅ **User Dashboard** - View history and statistics  
✅ **Responsive Design** - Works on desktop/mobile  
✅ **Persistent Storage** - Data survives restarts  
✅ **Clean Architecture** - Well-organized codebase  

---

## 📧 Support

For issues:
1. Check the **Troubleshooting** section
2. Open **browser console** (F12)
3. Check **backend logs**

---

**Version**: 1.0.0  
**Last Updated**: April 13, 2026  
**Status**: ✅ Production Ready
# RealTime Diabetes Prediction Application

A full-stack application for diabetes prediction using Spring Boot backend with Firebase Firestore and a React/HTML frontend.

## Project Structure

```
RealTime/
├── backend/                          # Spring Boot REST API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/demo/
│   │   │   │   ├── config/          # Firebase configuration
│   │   │   │   ├── controller/      # REST endpoints
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── entity/          # Firestore entities
│   │   │   │   ├── repository/      # Firestore repositories
│   │   │   │   ├── service/         # Business logic
│   │   │   │   ├── util/            # Utilities
│   │   │   │   └── Application.java # Main bootstrap
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── firebase-adminsdk.json.json
│   │   └── test/
│   └── pom.xml                       # Maven configuration
│
├── frontend/                         # HTML/CSS/JS UI (Single App)
│   ├── app.html                      # ★ MAIN FILE (Login → Register → Dashboard)
│   ├── APP_GUIDE.md                  # Complete user guide
│   ├── firebase-config.js
│   ├── css/                          # Optional custom styles
│   ├── js/                           # Optional scripts
│   └── public/                       # Static assets
│
├── ml/                               # Machine Learning models
│   └── diabetes/
│       ├── src/
│       │   └── diabetes_prediction.py
│       └── requirements.txt
│
├── data/                             # Training data
│   └── diabetes.csv
│
├── apache-maven-3.9.6/              # Maven build tool
│
└── FIREBASE_SETUP.md                # Firebase setup guide

```

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.9.6 (included locally)
- Firebase Project with Firestore enabled
- Firebase Service Account key

### 1. Configure Firebase

1. Download service account key from Firebase Console
2. Place in: `backend/src/main/resources/firebase-adminsdk.json.json`
3. Update Firebase project ID in: `backend/src/main/resources/application.properties`

See [FIREBASE_SETUP.md](FIREBASE_SETUP.md) for detailed instructions.

### 2. Build Backend

```bash
cd backend
../apache-maven-3.9.6/bin/mvn.cmd clean install
```

### 3. Run Backend

**Terminal 1:**
```bash
cd backend
java -jar target/realtime-app-1.0.0.jar
```

Server runs on: **http://localhost:8080**

### 4. Open Frontend

**Terminal 2:**
```bash
# Simply open in your browser:
file:///C:/Users/mekal/.vscode/projectjava/RealTime/frontend/app.html
```

Or navigate to: `frontend/app.html`

See [APP_GUIDE.md](frontend/APP_GUIDE.md) for complete user guide.

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login

### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Predictions
- `POST /api/predictions/predict` - Make diabetes prediction
- `GET /api/predictions/user/{userId}` - Get user's predictions
- `GET /api/predictions/{id}` - Get prediction details

## Database
- **Type**: Firebase Firestore (NoSQL)
- **Collections**:
  - `users` - User profiles
  - `diabetes_predictions` - Prediction records

## Technology Stack
- **Backend**: Spring Boot 3.2.4, Java 21
- **Database**: Firebase Firestore
- **Frontend**: HTML5, CSS3, JavaScript
- **Build**: Maven 3.9.6
- **ML**: Python (diabetes prediction model)

## Features
✅ User authentication & registration
✅ Diabetes prediction based on medical metrics
✅ Prediction history tracking
✅ Real-time data with Firebase
✅ RESTful API
✅ Responsive UI
✅ **Single-file frontend** (app.html) - No separate pages needed

## Frontend Architecture
**app.html** is a consolidated single-file application that includes:
- 🔐 **Login Page** - Default view with username/password
- 📝 **Register Page** - Create new account with email
- 📊 **Dashboard** - Prediction form + History display
- ✅ **Persistent Sessions** - Auto-login on page reload
- 📱 **Responsive Design** - Works on desktop and mobile

All features in ONE HTML file with dynamic view switching. No separate login.html, register.html, dashboard.html files needed!

See [frontend/APP_GUIDE.md](frontend/APP_GUIDE.md) for complete user guide.

## Notes
- Maven is included locally: `apache-maven-3.9.6/`
- Firebase credentials are required to run
- ML model integrated via Python script in `ml/diabetes/`
- **Frontend**: Open `frontend/app.html` to start - all features in one file!
- **No build required for frontend** - Just open in browser
