package com.swc4253groupd.libraryapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    public SecurityConfig() {
        // Explicit constructor (can be omitted if not injecting dependencies)
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login.html",
                                "/addborrowbook.html",
                                "/addnewbook.html",
                                "/addnewuser.html",
                                "/bookborrowmanagement.html",
                                "/bookmanagement.html",
                                "/borrowbook.html",
                                "/edituser.html",
                                "/latereturn.html",
                                "/student.html",
                                "/userbook.html",
                                "/usermanagement.html",
                                "/script.js",
                                "/b1.png",
                                "/b2.png",
                                "/b4.png",
                                "/favicon.ico",
                                "/api/**")
                        .permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}