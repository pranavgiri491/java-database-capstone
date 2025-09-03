package com.project.backend.services;



import com.project.backend.models.*;
import com.project.backend.repository.*;
import com.project.backend.models.Patient;
import com.project.backend.repository.AppointmentRepository;
import com.project.backend.repository.DoctorRepository;
import com.project.backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TokenService tokenService;

    /**
     * Book a new appointment
     * @param appointment Appointment object to be saved
     * @return 1 if successful, 0 if error
     */
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Update an existing appointment
     * @param appointment Updated appointment details
     * @return ResponseEntity with success or error message
     */
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingAppointmentOpt = appointmentRepository.findById(appointment.getId());
        if (existingAppointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // ✅ Optional: validate doctor and patient IDs exist
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctorId());
        Optional<Patient> patientOpt = patientRepository.findById(appointment.getPatientId());
        if (doctorOpt.isEmpty() || patientOpt.isEmpty()) {
            response.put("message", "Invalid doctor or patient information.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error updating appointment.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Cancel an appointment
     * @param id Appointment ID
     * @param token Authorization token
     * @return ResponseEntity with success or error message
     */
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            response.put("message", "Appointment not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Appointment appointment = appointmentOpt.get();
        Long patientIdFromToken = tokenService.extractUserId(token);

        if (!Objects.equals(appointment.getPatientId(), patientIdFromToken)) {
            response.put("message", "You are not authorized to cancel this appointment.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error cancelling appointment.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get appointments for a doctor on a specific date
     * @param pname Patient name filter (optional)
     * @param date Date of appointments
     * @param token Authorization token
     * @return Map containing appointments list
     */
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        Long doctorIdFromToken = tokenService.extractUserId(token);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorIdFromToken, startOfDay, endOfDay
        );

        if (pname != null && !pname.isEmpty()) {
            appointments.removeIf(a -> !a.getPatientName().toLowerCase().contains(pname.toLowerCase()));
        }

        response.put("appointments", appointments);
        return response;
    }
}
