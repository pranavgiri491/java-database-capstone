package com.project.back_end.models;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Specialty is required")
    @Size(min = 3, max = 50, message = "Specialty must be between 3 and 50 characters")
    @Column(nullable = false, length = 50)
    private String specialty;

    @NotNull(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    @Column(nullable = false, length = 10)
    private String phone;

    @ElementCollection
    @CollectionTable(name = "doctor_available_times", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "available_time")
    private List<String> availableTimes = new ArrayList<>();

    // Default constructor (required by JPA)
    public Doctor() {
    }

    // Parameterized constructor for convenience
    public Doctor(String name, String specialty, String email, String password, String phone) {
        this.name = name;
        this.specialty = specialty;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    // Helper method to add available time slot
    public void addAvailableTime(String timeSlot) {
        if (availableTimes == null) {
            availableTimes = new ArrayList<>();
        }
        // Validate time slot format (optional: "HH:MM - HH:MM")
        if (timeSlot != null && !timeSlot.trim().isEmpty()) {
            availableTimes.add(timeSlot.trim());
        }
    }

    // Helper method to remove available time slot
    public void removeAvailableTime(String timeSlot) {
        if (availableTimes != null) {
            availableTimes.remove(timeSlot);
        }
    }

    // Helper method to clear all available times
    public void clearAvailableTimes() {
        if (availableTimes != null) {
            availableTimes.clear();
        }
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }

    // Utility methods

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", specialty='" + specialty + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", availableTimes=" + availableTimes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Doctor doctor = (Doctor) o;
        return id != null && id.equals(doctor.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}