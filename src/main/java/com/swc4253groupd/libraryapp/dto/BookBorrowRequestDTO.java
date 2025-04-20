package com.swc4253groupd.libraryapp.dto;

import java.time.LocalDate;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookBorrowRequestDTO {
    @NotBlank
    @NonNull
    private Integer bookid;

    @NotBlank
    @NonNull
    private Integer userid;
    
    @NotBlank
    @NonNull
    private LocalDate dateborrowed;

    @NotBlank
    @NonNull
    private LocalDate datereturn;
    
    @NotBlank
    @NonNull
    private boolean isreturn;
}
