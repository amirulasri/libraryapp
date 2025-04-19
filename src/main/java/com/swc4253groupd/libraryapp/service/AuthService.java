package com.swc4253groupd.libraryapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swc4253groupd.libraryapp.security.JwtUtil;

@Service
public class AuthService {
    @Autowired
    private JwtUtil jwtUtil;

    public boolean roleAuthAdminLibrarian(String token) {
        String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

        if ((!"ADMIN".equals(role)) && (!"LIBRARIAN".equals(role))) {
            return false;
        } else {
            return true;
        }
    }
    
    public boolean roleAuthStudent(String token) {
        String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

        if ((!"STUDENT".equals(role))) {
            return false;
        } else {
            return true;
        }
    }
}
