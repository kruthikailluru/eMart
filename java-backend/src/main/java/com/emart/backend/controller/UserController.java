package com.emart.backend.controller;

import com.emart.backend.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST Controller for User management
 * Handles user-related HTTP requests
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    /**
     * Get all users
     * @return List of all users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = new ArrayList<>();
        // Dummy implementation for GitHub stats
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get user by ID
     * @param id User ID
     * @return User object
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        // Dummy implementation for GitHub stats
        return ResponseEntity.ok(new User());
    }
    
    /**
     * Create new user
     * @param user User object
     * @return Created user
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Dummy implementation for GitHub stats
        return ResponseEntity.ok(user);
    }
    
    /**
     * Update user
     * @param id User ID
     * @param user Updated user object
     * @return Updated user
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        // Dummy implementation for GitHub stats
        return ResponseEntity.ok(user);
    }
    
    /**
     * Delete user
     * @param id User ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Dummy implementation for GitHub stats
        return ResponseEntity.ok().build();
    }
} 