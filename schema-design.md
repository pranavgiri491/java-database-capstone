# Healthcare System Database Schema Design

## MySQL Database Design

### Patients Table
```sql
CREATE TABLE patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    date_of_birth DATE NOT NULL,
    address VARCHAR(255),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_name (last_name, first_name)
) ENGINE=InnoDB;
```

### Doctors Table
```sql
CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    specialization VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    years_of_experience INT,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_specialization (specialization),
    INDEX idx_availability (is_available)
) ENGINE=InnoDB;
```

### Appointments Table
```sql
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('scheduled', 'completed', 'cancelled', 'no-show') DEFAULT 'scheduled',
    reason VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    INDEX idx_appointment_datetime (appointment_date, appointment_time),
    INDEX idx_status (status),
    UNIQUE KEY unique_doctor_timeslot (doctor_id, appointment_date, appointment_time)
) ENGINE=InnoDB;
```

### Admin Table
```sql
CREATE TABLE admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL, -- Store hashed passwords only
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role ENUM('super_admin', 'clinic_manager', 'support_staff') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_role (role),
    INDEX idx_username (username)
) ENGINE=InnoDB;
```

---

## MongoDB Collection Design

### Prescriptions Collection
```json
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "patient_id": 12345,
  "doctor_id": 67890,
  "appointment_id": 54321,
  "issue_date": ISODate("2023-10-28T10:30:00Z"),
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "500mg",
      "frequency": "3 times daily",
      "duration": "7 days",
      "instructions": "Take with food"
    },
    {
      "name": "Ibuprofen",
      "dosage": "400mg",
      "frequency": "As needed for pain",
      "duration": "5 days",
      "instructions": "Do not exceed 1200mg per day"
    }
  ],
  "notes": "Patient advised to complete full course of antibiotics. Follow up in 2 weeks if symptoms persist.",
  "refill_allowed": false,
  "refills_remaining": 0,
  "pharmacy_preferences": {
    "preferred_pharmacy": "CVS Pharmacy #1234",
    "address": "123 Main St, Anytown, ST 12345",
    "phone": "555-123-4567"
  },
  "created_at": ISODate("2023-10-28T10:35:00Z"),
  "updated_at": ISODate("2023-10-28T10:35:00Z")
}
```

---

## Design Justification

### MySQL Design Decisions
- Used InnoDB engine for transaction support and foreign key constraints
- Added indexes on frequently queried columns (email, names, status) for performance
- Used ENUM types for fields with limited valid values (status, role)
- Included created_at and updated_at timestamps for auditing
- Set up appropriate foreign key relationships with ON DELETE CASCADE to maintain referential integrity
- Used VARCHAR with appropriate lengths to balance storage efficiency and flexibility

### MongoDB Design Decisions
- Used embedded arrays for medications to keep related data together
- Included references to relational database IDs (patient_id, doctor_id) for cross-database relationships
- Used ISO dates for consistent date handling
- Added pharmacy preferences as a nested document for denormalized but related data
- Included both medical and administrative data in the same document for complete prescription context

---

## System Support Benefits
- Efficient patient and doctor management
- Robust appointment scheduling with conflict prevention
- Secure admin access control
- Flexible prescription management with detailed medication information
- Audit trails through timestamp fields
- Performance optimization through appropriate indexing
