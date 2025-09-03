package com.project.backend.services;
package com.example.service;

import com.example.dto.Login;
import com.example.model.Doctor;
import com.example.model.Appointment;
import com.example.repository.DoctorRepository;
import com.example.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    /**
     * Fetch doctor availability for a specific date
     */
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        // Example: available slots every hour from 9 AM - 5 PM
        List<String> allSlots = new ArrayList<>();
        for (int i = 9; i <= 17; i++) {
            allSlots.add(i + ":00");
        }

        // Fetch booked slots
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        List<Appointment> bookedAppointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);

        Set<String> bookedSlots = bookedAppointments.stream()
                .map(a -> a.getAppointmentTime().getHour() + ":00")
                .collect(Collectors.toSet());

        // Return available slots
        return allSlots.stream().filter(slot -> !bookedSlots.contains(slot)).collect(Collectors.toList());
    }

    /**
     * Save a new doctor
     */
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1; // doctor already exists
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Update an existing doctor
     */
    public int updateDoctor(Doctor doctor) {
        if (doctor.getId() == null || !doctorRepository.existsById(doctor.getId())) {
            return -1;
        }
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get all doctors
     */
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Delete a doctor
     */
    public int deleteDoctor(long id) {
        if (!doctorRepository.existsById(id)) {
            return -1;
        }
        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Validate doctor login
     */
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());

        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid credentials.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = tokenService.generateToken(doctor.getId(), "DOCTOR");
        response.put("token", token);
        response.put("message", "Login successful.");
        return ResponseEntity.ok(response);
    }

    /**
     * Find doctors by partial name
     */
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorRepository.findByNameLike(name));
        return response;
    }

    /**
     * Filter doctors by name, specialty, and time
     */
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    /**
     * Filter doctors by name and time
     */
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    /**
     * Filter doctors by name and specialty
     */
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty));
        return response;
    }

    /**
     * Filter doctors by specialty and time
     */
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    /**
     * Filter doctors by specialty only
     */
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorRepository.findBySpecialtyIgnoreCase(specialty));
        return response;
    }

    /**
     * Filter all doctors by time
     */
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findAll();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    /**
     * Private helper: filter doctors by available times (AM/PM)
     */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.isBlank()) {
            return doctors;
        }

        return doctors.stream()
                .filter(d -> {
                    if ("AM".equalsIgnoreCase(amOrPm)) {
                        return d.getAvailableFrom().getHour() < 12;
                    } else if ("PM".equalsIgnoreCase(amOrPm)) {
                        return d.getAvailableTo().getHour() >= 12;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
