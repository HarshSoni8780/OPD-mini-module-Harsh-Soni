# OPD Mini-Module

A small full-stack Outpatient Department (OPD) system covering **patient registration**, **appointment booking**, and **doctor consultation summaries**.

Built with **Spring Boot**, **Spring Data JPA**, **Angular**, and **MySQL**.

---

## Features

### Patients
- Register a patient (name, gender, age, phone)
- List all patients (newest first)
- Search by name or phone (partial match)
- Validation, with unique phone numbers

### Appointments
- Book an appointment for a patient with a doctor at a date/time
- Prevents past bookings and double booking for the same doctor and time
- View today's appointments, sorted by time

### Consultations
- Doctor enters vitals (Blood Pressure, Temperature) and notes
- Saving marks the consultation complete and the appointment `COMPLETED` in a single transaction
- View a patient's completed consultations (newest first)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot, Spring Data JPA, Bean Validation |
| Frontend | Angular, Reactive Forms, HttpClient |
| Database | MySQL |
| Build tools | Maven, npm / Angular CLI |

---

## Architecture

```
Angular UI  --(REST/JSON)-->  Spring Boot  -->  MySQL
                              Controller
                                 |
                              Service  (business rules, transactions)
                                 |
                              Repository (JPA)
```

---

## Project Structure

```
opd-module/
├── backend/
│   └── src/main/java/com/example/opd/
│       ├── config/        CORS configuration
│       ├── controller/    REST controllers
│       ├── service/       Business logic
│       ├── repository/    JPA repositories
│       ├── entity/        Patient, Doctor, Appointment, Consultation
│       ├── dto/           Request/response objects
│       └── exception/     Custom exceptions + global handler
├── frontend/
│   └── src/app/
│       ├── models/
│       ├── services/
│       └── pages/         patients, appointments, consultation
└── README.md
```

Adjust folder names if your layout differs.

---

## Prerequisites

- JDK 17+
- Maven 3.8+
- Node.js 18+ and npm
- Angular CLI (`npm install -g @angular/cli`)
- MySQL 8+

---

## Setup and Run

### 1. Database
```sql
CREATE DATABASE opd_db;
```
Tables are created automatically by JPA (`ddl-auto=update`). Doctors are seeded from `data.sql` on startup.

### 2. Backend
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/opd_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
```
Run:
```bash
cd backend
mvn spring-boot:run
```
API runs at `http://localhost:8080`.

### 3. Frontend
```bash
cd frontend
npm install
ng serve
```
UI runs at `http://localhost:4200`.

---

## UI Screens

| Route | Screen |
|---|---|
| `/patients` | Patient register, list, and search |
| `/appointments` | Book appointment and today's list |
| `/consultation/:appointmentId` | Consultation form and patient history |

---

## REST API

Base URL: `http://localhost:8080/api`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/patients` | Register a patient |
| GET | `/patients?search=` | List or search patients |
| GET | `/patients/{id}` | Get a patient |
| GET | `/doctors` | List doctors |
| POST | `/appointments` | Book an appointment |
| GET | `/appointments/today` | Today's appointments |
| GET | `/appointments/{id}` | Get an appointment |
| POST | `/appointments/{id}/consultation` | Save consultation and complete appointment |
| GET | `/patients/{id}/consultations` | Completed consultations for a patient |

### Sample Requests

**Register patient**
```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"name":"Ravi Shah","gender":"MALE","age":34,"phone":"9876543210"}'
```

**Book appointment**
```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":1,"doctorId":2,"appointmentTime":"2026-10-08T10:30:00"}'
```

**Complete consultation**
```bash
curl -X POST http://localhost:8080/api/appointments/1/consultation \
  -H "Content-Type: application/json" \
  -d '{"bloodPressure":"120/80","temperature":98.6,"notes":"Mild fever, advised rest and fluids."}'
```

### Error Format
```json
{
  "timestamp": "2026-10-07T11:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Doctor already has an appointment at this time"
}
```

| Status | Meaning |
|---|---|
| 400 | Validation failed |
| 404 | Resource not found |
| 409 | Duplicate phone, double booking, or consultation already completed |

---

## Data Model

```
Patient 1 ──── N Appointment N ──── 1 Doctor
                    │
                    1
                    │
                    1
               Consultation
```

| Table | Key columns |
|---|---|
| patient | id, name, gender, age, phone (unique), created_at |
| doctor | id, name, specialization |
| appointment | id, patient_id, doctor_id, appointment_time, status, created_at; unique (doctor_id, appointment_time) |
| consultation | id, appointment_id (unique), blood_pressure, temperature, notes, completed_at |

---

## Validation Rules

| Field | Rule |
|---|---|
| Patient name | Required, 2-100 characters |
| Gender | `MALE`, `FEMALE`, or `OTHER` |
| Age | 0-120 |
| Phone | Exactly 10 digits, unique |
| Appointment time | Not in the past; doctor slot must be free |
| Blood pressure | Format like `120/80` |
| Temperature | 90-110 (°F) |
| Notes | Required, max 500 characters |

---

## Design Decisions

- **DTOs** are used so entities are never exposed through the API.
- **`@Transactional`** on consultation completion keeps the consultation record and appointment status in sync.
- **Double booking** is checked in the service layer and enforced again by a database unique constraint.
- **Global exception handler** (`@RestControllerAdvice`) gives every error the same shape.
- **CORS** is enabled for `http://localhost:4200`.

---

## Assumptions

- Doctors are seeded; there is no doctor management screen.
- Vitals captured are Blood Pressure and Temperature.
- Single clinic and single timezone (server local time).
- No authentication (optional, out of scope).

---

## Demo Flow

1. Register a patient (try an invalid and a duplicate phone to show validation).
2. Search by name, then by phone.
3. Book an appointment, then attempt the same slot again to show the conflict error.
4. Open today's appointments and click **Consult**.
5. Enter vitals and notes, then complete the consultation.
6. Show the patient's consultation history.

---

## Future Improvements

- JWT login with roles (doctor, receptionist)
- Cancel and reschedule appointments
- Pagination and sorting
- Prescriptions and reports dashboard
- Docker Compose for MySQL and the app
- Unit and integration tests (JUnit, Mockito)

---

## Author

Harsh
