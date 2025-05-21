# Healthcare Appointment System

A robust appointment management system built with Spring Boot that facilitates the scheduling and management of medical appointments between patients and doctors.

## 🚀 Features

- Appointment scheduling and management
- Doctor and patient management
- Real-time appointment status tracking
- Secure authentication and authorization
- Specialized doctor search functionality
- Appointment history and tracking

## 🛠 Technology Stack

- **Backend Framework**: Spring Boot
- **Security:** Spring Security with JWT
- **Database Access:** Spring Data JPA
- **API Documentation:** SpringDoc OpenAPI
- **Build Tool:** Maven
- **Java Version:** Java 24
- **Database:** PostgreSQL

## 📁 Project Structure

```plaintext
healthcare-appointment-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/healthcare/appointmentsystem/
│   │   │       ├── config/              # Application configurations
│   │   │       ├── controller/          # REST API controllers
│   │   │       ├── dto/                 # Data Transfer Objects
│   │   │       ├── exception/           # Custom exceptions and handlers
│   │   │       ├── model/              # Entity models
│   │   │       │   ├── Appointment.java
│   │   │       │   ├── AppointmentStatus.java
│   │   │       │   ├── BloodType.java
│   │   │       │   ├── Doctor.java
│   │   │       │   ├── Gender.java
│   │   │       │   ├── Patient.java
│   │   │       │   └── User.java
│   │   │       ├── repository/         # Data access layer
│   │   │       ├── security/           # Security configurations
│   │   │       ├── service/           # Business logic implementation
│   │   │       ├── util/              # Utility classes
│   │   │       └── AppointmentSystemApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/                # Static resources
│   │       ├── templates/             # Template files
│   │       ├── application.yml        # Main application configuration
│   │       ├── application-dev.yml    # Development configuration
│   │       └── application-prod.yml   # Production configuration
│   │
│   └── test/                         # Test files
│
├── .mvn/                             # Maven wrapper directory
├── .gitignore                        # Git ignore file
├── .gitattributes                    # Git attributes file
├── mvnw                              # Maven wrapper script (Unix)
├── mvnw.cmd                          # Maven wrapper script (Windows)
└── pom.xml                           # Project dependencies and build configuration
```

## 🏃‍♂️ Getting Started

### Prerequisites

- Java 24 or higher
- Maven 3.6 or higher
- PostgreSQL installed and running

### Installation

1. Clone the repository: bash git clone https://github.com/AbiyathRahman/appointment-system.git
2. Navigate to the project directory: bash cd appointment-system
3. Build the project: bash nvm clean install
4. Run the application: bash mvn spring-boot:run


The application will start running at `http://localhost:8080`

## 🔑 API Endpoints

### Appointment Management
- `POST /api/appointments` - Create new appointment
- `GET /api/appointments` - Get all appointments
- `GET /api/appointments/{id}` - Get appointment by ID
- `PUT /api/appointments/{id}` - Update appointment
- `DELETE /api/appointments/{id}` - Delete appointment

### Doctor Management
- `POST /api/doctors` - Register new doctor
- `GET /api/doctors` - Get all doctors
- `GET /api/doctors/{id}` - Get doctor by ID
- `PUT /api/doctors/{id}` - Update doctor information
- `DELETE /api/doctors/{id}` - Delete doctor

### Patient Management
- `POST /api/patients` - Register new patient
- `GET /api/patients` - Get all patients
- `GET /api/patients/{id}` - Get patient by ID
- `PUT /api/patients/{id}` - Update patient information
- `DELETE /api/patients/{id}` - Delete patient

## 🔒 Security

The application uses Spring Security with JWT (JSON Web Tokens) for authentication and authorization. All endpoints except the authentication endpoints require a valid JWT token in the Authorization header.

## 🧪 Testing

To run the tests: bash mvn test


## 📝 Configuration

Key application properties can be configured in `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:your-database-url
spring.datasource.username=your-username
spring.datasource.password=your-password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=your-secret-key
jwt.expiration=86400000
```
## 🤝 Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👥 Authors
- Abiyath Rahman - _Initial work_

## 🙏 Acknowledgments
- Spring Boot team for the excellent framework
- The open-source community

