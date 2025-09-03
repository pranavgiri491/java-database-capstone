package com.project.backend.services;

package com.example.hms.service;

import com.example.hms.model.Admin;
import com.example.hms.model.Doctor;
import com.example.hms.model.Patient;
import com.example.hms.repository.AdminRepository;
import com.example.hms.repository.DoctorRepository;
import com.example.hms.repository.PatientRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}") // should be set in application.properties
    private String secret;

    private SecretKey signingKey;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // Initialize the signing key after secret is injected
    @PostConstruct
    private void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * ✅ Generate JWT token
     */
    public String generateToken(String identifier) {
        return Jwts.builder()
                .setSubject(identifier) // email or username
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 604800000)) // 7 days
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ Extract identifier (email/username) from token
     */
    public String extractIdentifier(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * ✅ Validate token for specific user type
     */
    public boolean validateToken(String token, String userType) {
        try {
            String identifier = extractIdentifier(token);

            switch (userType.toLowerCase()) {
                case "admin":
                    Optional<Admin> admin = adminRepository.findByUsername(identifier);
                    return admin.isPresent();

                case "doctor":
                    Optional<Doctor> doctor = doctorRepository.findByEmail(identifier);
                    return doctor.isPresent();

                case "patient":
                    Optional<Patient> patient = patientRepository.findByEmail(identifier);
                    return patient.isPresent();

                default:
                    return false;
            }

        } catch (JwtException | IllegalArgumentException e) {
            // Invalid or expired token
            return false;
        }
    }

    /**
     * ✅ Get signing key (optional helper method)
     */
    public SecretKey getSigningKey() {
        return signingKey;
    }
}
