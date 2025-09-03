package com.project.backend.repository;



import com.project.backend.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Custom finder method to get Admin by username
    Admin findByUsername(String username);
}
