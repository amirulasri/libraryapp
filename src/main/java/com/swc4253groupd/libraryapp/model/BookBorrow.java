package com.swc4253groupd.libraryapp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BookBorrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookborrowid;

    private LocalDateTime dateborrowed;

    private LocalDateTime datereturn;

    @ManyToOne
    @JoinColumn(name = "bookid", nullable = false)
    private Book book;
    
    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private User user;
}
