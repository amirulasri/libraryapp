package com.swc4253groupd.libraryapp.dto;

import java.time.Year;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequestDTO {
    @NotBlank
    @NonNull
    private String title;

    @NotBlank
    @NonNull
    private String author;
    
    @NotBlank
    @NonNull
    private String category;

    @NotBlank
    @NonNull
    private Year yearpublished;
}
