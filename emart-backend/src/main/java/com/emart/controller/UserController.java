package com.emart.controller;

import com.emart.model.User;
import com.emart.service.UserService;
import com.emart.service.JwtService;
import com.emart.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    private final JwtService jwtService;
    private final EmailService emailService;
    
    // Admin endpoints
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            String role = jwtService.getRoleFromToken(token);
            
            // Only admins can access this endpoint
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get all users: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/enabled")
    public ResponseEntity<?> getEnabledUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            String role = jwtService.getRoleFromToken(token);
            
            // Only admins can access this endpoint
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            List<User> users = userService.getEnabledUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get enabled users: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@RequestHeader("Authorization") String authHeader,
                                          @PathVariable String role) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            String adminRole = jwtService.getRoleFromToken(token);
            
            // Only admins can access this endpoint
            if (!"ADMIN".equals(adminRole)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            List<User> users = userService.getUsersByRole(userRole);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get users by role: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable String userId) {
        try {
            String token = authHeader.substring(7);
            String currentUserId = jwtService.extractUsername(token);
            String role = jwtService.getRoleFromToken(token);
            
            // Users can only access their own profile, admins can access any profile
            if (!"ADMIN".equals(role) && !currentUserId.equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to get user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authHeader,
                                      @PathVariable String userId,
                                      @RequestBody User userDetails) {
        try {
            String token = authHeader.substring(7);
            String currentUserId = jwtService.extractUsername(token);
            String role = jwtService.getRoleFromToken(token);
            
            // Users can only update their own profile, admins can update any profile
            if (!"ADMIN".equals(role) && !currentUserId.equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            User updatedUser = userService.updateUser(userId, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Failed to update user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader,
                                      @PathVariable String userId) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            String role = jwtService.getRoleFromToken(token);
            
            // Only admins can delete users
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            userService.deleteUser(userId);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{userId}/enable")
    public ResponseEntity<?> enableUser(@RequestHeader("Authorization") String authHeader,
                                      @PathVariable String userId) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            String role = jwtService.getRoleFromToken(token);
            
            // Only admins can enable users
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            userService.enableUser(userId);
            return ResponseEntity.ok(Map.of("message", "User enabled successfully"));
        } catch (Exception e) {
            log.error("Failed to enable user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{userId}/disable")
    public ResponseEntity<?> disableUser(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable String userId) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            String role = jwtService.getRoleFromToken(token);
            
            // Only admins can disable users
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            userService.disableUser(userId);
            return ResponseEntity.ok(Map.of("message", "User disabled successfully"));
        } catch (Exception e) {
            log.error("Failed to disable user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestHeader("Authorization") String authHeader,
                                       @RequestParam String query) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            String role = jwtService.getRoleFromToken(token);
            
            // Only admins can search users
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            // This would typically use a more sophisticated search
            // For now, we'll search by username or email containing the query
            List<User> allUsers = userService.getAllUsers();
            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                                  user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                                  (user.getFirstName() + " " + user.getLastName()).toLowerCase().contains(query.toLowerCase()))
                    .toList();
            
            return ResponseEntity.ok(filteredUsers);
        } catch (Exception e) {
            log.error("Failed to search users: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String adminId = jwtService.extractUsername(token);
            String role = jwtService.getRoleFromToken(token);
            
            // Only admins can access user stats
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            List<User> allUsers = userService.getAllUsers();
            List<User> enabledUsers = userService.getEnabledUsers();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", allUsers.size());
            stats.put("enabledUsers", enabledUsers.size());
            stats.put("disabledUsers", allUsers.size() - enabledUsers.size());
            
            // Count by role
            Map<String, Long> roleCounts = allUsers.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                        user -> user.getRole().toString(),
                        java.util.stream.Collectors.counting()
                    ));
            stats.put("roleCounts", roleCounts);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Failed to get user stats: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 