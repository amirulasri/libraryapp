package com.swc4253groupd.libraryapp.dto;
import com.swc4253groupd.libraryapp.model.User;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    @NotBlank
    @NonNull
    private String username;

    @NotBlank
    @NonNull
    private String password;

    @NotBlank
    @NonNull
    private String name;

    @NotBlank
    @NonNull
    private String email;

    @NotNull
    private User.Role role;

    @NotBlank
    @NonNull
    private String address;

    @NotBlank
    @NonNull
    private String phoneno;
}