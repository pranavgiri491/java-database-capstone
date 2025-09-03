package com.project.backend.services;
package com.example.hms.service;

import com.example.hms.dto.AppointmentDTO;
import com.example.hms.model.Appointment;
import com.example.hms.model.Patient;
import com.example.hms.repository.AppointmentRepository;
import com.example.hms.repository.PatientRepository;
import com.example.hms.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    // 1. Create Patient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 2. Get Appointments for Patient (with token validation)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null || !patient.getId().equals(id)) {
                response.put("error", "Unauthorized access");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            List<Appointment> appointments = appointmentRepository.findByPatientId(id);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());

            response.put("appointments", appointmentDTOs);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "Failed to fetch appointments");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. Filter Appointments by Condition (past/future)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(id);

            List<AppointmentDTO> filteredAppointments = appointments.stream()
                    .filter(app -> "past".equalsIgnoreCase(condition) ?
                            app.getDateTime().isBefore(LocalDateTime.now()) :
                            app.getDateTime().isAfter(LocalDateTime.now()))
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());

            response.put("appointments", filteredAppointments);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "Filtering failed");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 4. Filter Appointments by Doctor Name
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            List<AppointmentDTO> filteredAppointments = appointments.stream()
                    .filter(app -> app.getDoctor().getName().equalsIgnoreCase(name))
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());

            response.put("appointments", filteredAppointments);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "Filtering by doctor failed");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. Filter by Doctor + Condition
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);

            List<AppointmentDTO> filteredAppointments = appointments.stream()
                    .filter(app -> app.getDoctor().getName().equalsIgnoreCase(name))
                    .filter(app -> "past".equalsIgnoreCase(condition) ?
                            app.getDateTime().isBefore(LocalDateTime.now()) :
                            app.getDateTime().isAfter(LocalDateTime.now()))
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());

            response.put("appointments", filteredAppointments);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "Filtering by doctor and condition failed");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 6. Get Patient Details from Token
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("error", "Patient not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.put("patient", patient);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "Failed to fetch patient details");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

