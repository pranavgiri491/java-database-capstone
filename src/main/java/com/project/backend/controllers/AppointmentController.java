package com.project.backend.controllers;



import com.project.backend.models.*;
import com.project.backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "admin")
public class AdminController {

    private final Service service;

    @Autowired
    public AdminController(Service service) {
        this.service = service;
    }

    /**
     * ✅ Admin login endpoint
     * URL: POST /api/admin/login
     * Body: { "username": "admin", "password": "pass123" }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }
}
