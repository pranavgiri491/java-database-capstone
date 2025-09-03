package com.project.backend.controllers;



import com.project.backend.models.*;
import com.project.backend.services.AppointmentService;
import com.project.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    /**
     * ✅ Get appointments for a doctor by date & patient name (Doctor-only access)
     * URL: GET /appointments/{date}/{patientName}/{token}
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String date,
                                             @PathVariable String patientName,
                                             @PathVariable String token) {
        // Validate doctor token
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        LocalDate appointmentDate = LocalDate.parse(date);
        Map<String, Object> result = appointmentService.getAppointment(patientName, appointmentDate, token);
        return ResponseEntity.ok(result);
    }

    /**
     * ✅ Book an appointment (Patient-only access)
     * URL: POST /appointments/{token}
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@PathVariable String token,
                                                               @RequestBody Appointment appointment) {
        // Validate patient token
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        // Validate appointment slot
        int validationResult = service.validateAppointment(appointment);
        if (validationResult == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Doctor not found"));
        } else if (validationResult == 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Appointment time not available"));
        }

        int booked = appointmentService.bookAppointment(appointment);
        if (booked == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Appointment booked successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to book appointment"));
        }
    }

    /**
     * ✅ Update an appointment (Patient-only access)
     * URL: PUT /appointments/{token}
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@PathVariable String token,
                                                                 @RequestBody Appointment appointment) {
        // Validate patient token
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        return appointmentService.updateAppointment(appointment);
    }

    /**
     * ✅ Cancel an appointment (Patient-only access)
     * URL: DELETE /appointments/{id}/{token}
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable long id,
                                                                 @PathVariable String token) {
        // Validate patient token
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        return appointmentService.cancelAppointment(id, token);
    }
}
