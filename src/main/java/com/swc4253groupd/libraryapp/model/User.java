package com.swc4253groupd.libraryapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userid;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    private String password;

    private String name;

    private String email;

    public enum Role {
        STUDENT,
        LIBRARIAN,
        ADMIN
    }

    @Enumerated(EnumType.STRING)
    private Role role;

    private String address;

    private String phoneno;
}
