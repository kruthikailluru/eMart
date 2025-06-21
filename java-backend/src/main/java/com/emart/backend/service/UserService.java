package com.emart.backend.service;

import com.emart.backend.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for User business logic
 * Handles user-related operations
 */
@Service
public class UserService {
    
    private final List<User> users = new ArrayList<>();
    
    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    /**
     * Get user by ID
     * @param id User ID
     * @return Optional containing user if found
     */
    public Optional<User> getUserById(Long id) {
        return users.stream()
                   .filter(user -> user.getId().equals(id))
                   .findFirst();
    }
    
    /**
     * Create new user
     * @param user User to create
     * @return Created user
     */
    public User createUser(User user) {
        user.setId(generateId());
        users.add(user);
        return user;
    }
    
    /**
     * Update existing user
     * @param id User ID
     * @param user Updated user data
     * @return Updated user
     */
    public Optional<User> updateUser(Long id, User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                user.setId(id);
                users.set(i, user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Delete user by ID
     * @param id User ID
     * @return true if deleted, false if not found
     */
    public boolean deleteUser(Long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }
    
    /**
     * Generate unique ID for new users
     * @return Generated ID
     */
    private Long generateId() {
        return users.stream()
                   .mapToLong(User::getId)
                   .max()
                   .orElse(0) + 1;
    }
} 