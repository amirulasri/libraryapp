package com.swc4253groupd.libraryapp.dto;

import java.time.Year;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequestDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotBlank
    private Year yearpublished;
}
