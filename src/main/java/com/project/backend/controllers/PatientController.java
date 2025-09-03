package com.project.backend.controllers;


import com.project.backend.DTO.*;

import com.project.backend.models.*;
import com.project.backend.services.*;
 // Common service for validation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private Service service;

    /**
     * 1. Get Patient Details
     */
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatientDetails(@PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
        return ResponseEntity.ok(patientService.getPatientDetails(token));
    }

    /**
     * 2. Create a New Patient (Signup)
     */
    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody Patient patient) {
        try {
            if (patientService.patientExists(patient.getEmail(), patient.getPhone())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Patient with email id or phone no already exists"));
            }
            patientService.createPatient(patient);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Signup successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    /**
     * 3. Patient Login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody LoginDTO login) {
        return ResponseEntity.ok(service.validatePatientLogin(login));
    }

    /**
     * 4. Get Patient Appointments
     */
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long id, @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
        List<Appointment> appointments = patientService.getPatientAppointment(id);
        return ResponseEntity.ok(appointments);
    }

    /**
     * 5. Filter Patient Appointments
     */
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointments(@PathVariable String condition,
                                                       @PathVariable String name,
                                                       @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
        return ResponseEntity.ok(service.filterPatient(condition, name, token));
    }
}
