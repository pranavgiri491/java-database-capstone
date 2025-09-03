package com.example.hms.service;

import com.example.hms.dto.Login;
import com.example.hms.model.Admin;
import com.example.hms.model.Appointment;
import com.example.hms.model.Doctor;
import com.example.hms.model.Patient;
import com.example.hms.repository.AdminRepository;
import com.example.hms.repository.DoctorRepository;
import com.example.hms.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceClass {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public ServiceClass(TokenService tokenService,
                        AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository,
                        DoctorService doctorService,
                        PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /**
     * 1. Validate Token
     */
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("error", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        response.put("message", "Valid token");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 2. Validate Admin Login
     */
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

        if (admin == null || !admin.getPassword().equals(receivedAdmin.getPassword())) {
            response.put("error", "Invalid username or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(admin.getUsername());
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 3. Filter Doctor
     */
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        if (name != null && specialty != null && time != null) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (name != null && specialty != null) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (name != null && time != null) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (specialty != null && time != null) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (name != null) {
            return doctorService.findDoctorByName(name);
        } else if (specialty != null) {
            return doctorService.filterDoctorBySpecility(specialty);
        } else if (time != null) {
            return doctorService.filterDoctorsByTime(time);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("doctors", doctorService.getDoctors());
            return response;
        }
    }

    /**
     * 4. Validate Appointment
     */
    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctorId());
        if (!doctorOpt.isPresent()) {
            return -1; // doctor not found
        }

        List<String> availableSlots = doctorService.getDoctorAvailability(
                appointment.getDoctorId(),
                appointment.getAppointmentTime().toLocalDate()
        );

        return availableSlots.contains(appointment.getAppointmentTime().toString()) ? 1 : 0;
    }

    /**
     * 5. Validate Patient (for registration)
     */
    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    /**
     * 6. Validate Patient Login
     */
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(login.getIdentifier());

        if (patient == null || !patient.getPassword().equals(login.getPassword())) {
            response.put("error", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(patient.getEmail());
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 7. Filter Patient Appointments
     */
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.extractEmail(token);

        Patient patient = patientRepository.findByEmail(email);
        if (patient == null) {
            response.put("error", "Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        long patientId = patient.getId();
        if (condition != null && name != null) {
            return patientService.filterByDoctorAndCondition(condition, name, patientId);
        } else if (condition != null) {
            return patientService.filterByCondition(condition, patientId);
        } else if (name != null) {
            return patientService.filterByDoctor(name, patientId);
        }

        response.put("error", "No filter criteria provided");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
