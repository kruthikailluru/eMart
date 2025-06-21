package com.emart.controller;

import com.emart.model.User;
import com.emart.service.UserService;
import com.emart.service.JwtService;
import com.emart.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserService userService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            
            // Send welcome email
            emailService.sendWelcomeEmail(
                createdUser.getEmail(),
                createdUser.getFirstName() + " " + createdUser.getLastName(),
                createdUser.getRole().toString()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", createdUser);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByUsername(username);
            
            // Generate JWT token
            String token = jwtService.generateToken(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("user", user);
            response.put("role", user.getRole());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            String newToken = jwtService.refreshToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Token refreshed successfully");
            response.put("token", newToken);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody Map<String, String> passwordRequest) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            
            String oldPassword = passwordRequest.get("oldPassword");
            String newPassword = passwordRequest.get("newPassword");
            
            userService.changePassword(user.getId(), oldPassword, newPassword);
            
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            log.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Profile retrieval failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody User userDetails) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            User currentUser = userService.getUserByUsername(username);
            
            User updatedUser = userService.updateUser(currentUser.getId(), userDetails);
            
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Profile update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            boolean isValid = jwtService.validateToken(token);
            
            if (isValid) {
                String username = jwtService.extractUsername(token);
                String role = jwtService.getRoleFromToken(token);
                
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);
                response.put("role", role);
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(Map.of("valid", false));
            }
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In a real application, you might want to blacklist the token
        // For now, we'll just return a success message
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
} 