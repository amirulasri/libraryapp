package com.swc4253groupd.libraryapp.controller;

import com.swc4253groupd.libraryapp.model.User;
import com.swc4253groupd.libraryapp.repository.UserRepository;
import com.swc4253groupd.libraryapp.security.JwtUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(path = "/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // Explicit constructor for dependency injection
    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    private ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username).orElse(null);

        // Check user existence and password match in a single condition
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid username or password"));
        }

        String token = jwtUtil.generateToken(username, user.getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/profile")
    private ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Optional<User> user = userRepository.findByUsername(username);
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

}
