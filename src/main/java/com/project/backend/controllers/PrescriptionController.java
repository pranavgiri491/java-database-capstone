package com.project.backend.controllers;


import com.project.backend.models.*;
import com.project.backend.services.PrescriptionService;
import com.project.backend.services.*; // Common validation service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private Service service;

    /**
     * 1. Save Prescription
     */
    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(@PathVariable String token,
                                              @RequestBody Prescription prescription) {
        if (!service.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }

        try {
            prescriptionService.savePrescription(prescription);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Prescription saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save prescription"));
        }
    }

    /**
     * 2. Get Prescription by Appointment ID
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId,
                                             @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }

        Prescription prescription = prescriptionService.getPrescription(appointmentId);

        if (prescription == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No prescription found for this appointment"));
        }

        return ResponseEntity.ok(prescription);
    }
}
