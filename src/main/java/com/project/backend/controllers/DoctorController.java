package com.project.backend.controllers;



import com.project.backend.DTO.Login;
import com.project.backend.models.*;
import com.project.backend.services.DoctorService;
import com.project.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    /**
     * ✅ Get Doctor Availability (Doctor/Patient/Admin access)
     * URL: GET /doctor/availability/{user}/{doctorId}/{date}/{token}
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(@PathVariable String user,
                                                   @PathVariable long doctorId,
                                                   @PathVariable String date,
                                                   @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        LocalDate appointmentDate = LocalDate.parse(date);
        Map<String, Object> availability = doctorService.getDoctorAvailability(doctorId, appointmentDate);
        return ResponseEntity.ok(availability);
    }

    /**
     * ✅ Get List of Doctors
     * URL: GET /doctor
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(Map.of("doctors", doctors));
    }

    /**
     * ✅ Add New Doctor (Admin-only access)
     * URL: POST /doctor/{token}
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(@PathVariable String token,
                                                         @RequestBody Doctor doctor) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        int result = doctorService.saveDoctor(doctor);
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Doctor added to db"));
        } else if (result == 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Doctor already exists"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Some internal error occurred"));
        }
    }

    /**
     * ✅ Doctor Login
     * URL: POST /doctor/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    /**
     * ✅ Update Doctor Details (Admin-only access)
     * URL: PUT /doctor/{token}
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@PathVariable String token,
                                                            @RequestBody Doctor doctor) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        int result = doctorService.updateDoctor(doctor);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor updated"));
        } else if (result == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Doctor not found"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Some internal error occurred"));
        }
    }

    /**
     * ✅ Delete Doctor (Admin-only access)
     * URL: DELETE /doctor/{id}/{token}
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable long id,
                                                            @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (validation.getStatusCode() != HttpStatus.OK) {
            return validation;
        }

        int result = doctorService.deleteDoctor(id);
        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
        } else if (result == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Doctor not found with id " + id));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Some internal error occurred"));
        }
    }

    /**
     * ✅ Filter Doctors by Name, Time, and Specialty
     * URL: GET /doctor/filter/{name}/{time}/{speciality}
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(@PathVariable String name,
                                                             @PathVariable String time,
                                                             @PathVariable String speciality) {
        Map<String, Object> result = service.filterDoctor(name, speciality, time);
        return ResponseEntity.ok(result);
    }
}
