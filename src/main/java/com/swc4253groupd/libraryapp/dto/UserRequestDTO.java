package com.swc4253groupd.libraryapp.dto;
import com.swc4253groupd.libraryapp.model.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotNull
    private User.Role role;

    @NotBlank
    private String address;

    @NotBlank
    private String phoneno;
}