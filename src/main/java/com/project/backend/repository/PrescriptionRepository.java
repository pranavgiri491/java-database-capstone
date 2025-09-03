package com.project.backend.repository;


import com.project.backend.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    // Find prescriptions linked to a specific appointment ID
    List<Prescription> findByAppointmentId(Long appointmentId);
}
