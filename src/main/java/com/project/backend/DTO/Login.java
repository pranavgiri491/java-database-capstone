package com.project.backend.DTO;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class Login {

    private String identifier; // email (Doctor/Patient) or username (Admin)
    private String password;   // user-provided password

    // Default constructor (needed for deserialization)
    public Login() {}

    // Parameterized constructor
    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // Getters
    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @PostMapping("/login")
public ResponseEntity<?> loginUser(@RequestBody Login loginRequest) {
    String identifier = loginRequest.getIdentifier();
    String password = loginRequest.getPassword();
    // authentication logic here
    return ResponseEntity.ok("Login successful");
}


}
