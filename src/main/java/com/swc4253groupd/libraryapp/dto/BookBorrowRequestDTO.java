package com.swc4253groupd.libraryapp.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookBorrowRequestDTO {
    @NotBlank
    private Integer bookid;

    @NotBlank
    private Integer userid;
    
    @NotBlank
    private LocalDateTime dateborrowed;

    @NotBlank
    private LocalDateTime datereturn;
}
