package com.swc4253groupd.libraryapp.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.swc4253groupd.libraryapp.dto.UserRequestDTO;
import com.swc4253groupd.libraryapp.model.User;
import com.swc4253groupd.libraryapp.repository.UserRepository;
import com.swc4253groupd.libraryapp.security.JwtUtil;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    private ResponseEntity<?> getUsers(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String query) {
        try {
            String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

            // Only ADMIN can access
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
            }

            List<User> users;
            if (query != null && !query.isBlank()) {
                users = userRepository
                        .findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhonenoContainingIgnoreCase(
                                query, query, query, query);
            } else {
                users = (List<User>) userRepository.findAll();
            }

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    @GetMapping("/{id}")
    private ResponseEntity<?> getUserById(@PathVariable int id, @RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

            // Only allow admins to fetch user list
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get()); // Return the user object
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found")); // Return JSON error message
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    @PostMapping
    private ResponseEntity<?> registerUser(@RequestBody UserRequestDTO userRequest,
            @RequestHeader("Authorization") String token) {

        try {
            String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

            // Only allow admins to fetch user list
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Username already exists"));
            }

            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            user.setRole(userRequest.getRole());
            user.setPhoneno(userRequest.getPhoneno());
            user.setAddress(userRequest.getAddress());
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());

            User savedUser = userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    @PutMapping("/{id}")
    private ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody UserRequestDTO userRequest,
            @RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

            // Only allow admins to fetch user list
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            Optional<User> existingUser = userRepository.findById(id);
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                user.setUsername(userRequest.getUsername());
                if (userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
                    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
                }
                user.setRole(userRequest.getRole());
                user.setPhoneno(userRequest.getPhoneno());
                user.setAddress(userRequest.getAddress());
                user.setName(userRequest.getName());
                user.setEmail(userRequest.getEmail());

                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "User updated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<?> deleteUser(@PathVariable int id, @RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            if (!userRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }
}