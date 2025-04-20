package com.swc4253groupd.libraryapp.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swc4253groupd.libraryapp.dto.BookBorrowRequestDTO;
import com.swc4253groupd.libraryapp.model.Book;
import com.swc4253groupd.libraryapp.model.BookBorrow;
import com.swc4253groupd.libraryapp.model.User;
import com.swc4253groupd.libraryapp.notification.EmailService;
import com.swc4253groupd.libraryapp.repository.BookBorrowRepository;
import com.swc4253groupd.libraryapp.repository.BookRepository;
import com.swc4253groupd.libraryapp.repository.UserRepository;
import com.swc4253groupd.libraryapp.security.JwtUtil;
import com.swc4253groupd.libraryapp.service.AuthService;

@RestController
@RequestMapping("/api/bookborrows")
public class BookBorrowController {
    @Autowired
    private BookBorrowRepository bookBorrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    private ResponseEntity<?> getBookBorrows(@RequestHeader("Authorization") String token) {
        try {
            if (authService.roleAuthAdminLibrarian(token)) {
                List<BookBorrow> bookBorrows = (List<BookBorrow>) bookBorrowRepository.findAll();
                return ResponseEntity.ok(bookBorrows);
            } else if (authService.roleAuthStudent(token)) {
                Integer userid = jwtUtil.extractUserId(token.replace("Bearer ", ""));
                List<BookBorrow> bookBorrows = (List<BookBorrow>) bookBorrowRepository.findByUserUserid(userid);
                return ResponseEntity.ok(bookBorrows);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    @GetMapping("/{id}")
    private ResponseEntity<?> getBookBorrowById(@PathVariable int id, @RequestHeader("Authorization") String token) {
        try {
            if (!authService.roleAuthAdminLibrarian(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            Optional<BookBorrow> bookBorrow = bookBorrowRepository.findById(id);
            if (bookBorrow.isPresent()) {
                return ResponseEntity.ok(bookBorrow.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Borrowed book not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    @PostMapping
    private ResponseEntity<?> registerBookBorrow(@RequestBody BookBorrowRequestDTO bookBorrowRequest,
            @RequestHeader("Authorization") String token) {

        try {
            if (!authService.roleAuthAdminLibrarian(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            // GET USER AND BOOK FIRST
            Optional<Book> bookOpt = bookRepository.findById(bookBorrowRequest.getBookid());
            Optional<User> userOpt = userRepository.findById(bookBorrowRequest.getUserid());

            BookBorrow bookBorrow = new BookBorrow();
            bookBorrow.setDateborrowed(bookBorrowRequest.getDateborrowed());
            bookBorrow.setDatereturn(bookBorrowRequest.getDatereturn());
            bookBorrow.setBook(bookOpt.get());
            bookBorrow.setUser(userOpt.get());

            BookBorrow savedBookBorrow = bookBorrowRepository.save(bookBorrow);
            emailService.sendEmailAfterBook(userOpt.get().getEmail(), "Book borrow confirmation",
                    bookOpt.get().getTitle(), bookOpt.get().getAuthor(), savedBookBorrow.getDateborrowed().toString(),
                    savedBookBorrow.getDatereturn().toString());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "id", savedBookBorrow.getBookborrowid(),
                            "dateborrowed", savedBookBorrow.getDateborrowed(),
                            "datereturn", savedBookBorrow.getDatereturn(),
                            "book", savedBookBorrow.getBook(),
                            "user", savedBookBorrow.getUser()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }

    }

    @PutMapping("/{id}")
    private ResponseEntity<?> updateBookBorrow(@PathVariable int id,
            @RequestBody BookBorrowRequestDTO bookBorrowRequest,
            @RequestHeader("Authorization") String token) {
        try {
            if (!authService.roleAuthAdminLibrarian(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            Optional<Book> bookOpt = bookRepository.findById(bookBorrowRequest.getBookid());
            Optional<User> userOpt = userRepository.findById(bookBorrowRequest.getUserid());

            Optional<BookBorrow> existingBookBorrow = bookBorrowRepository.findById(id);
            if (existingBookBorrow.isPresent()) {
                BookBorrow bookBorrow = existingBookBorrow.get();
                bookBorrow.setDateborrowed(bookBorrowRequest.getDateborrowed());
                bookBorrow.setDatereturn(bookBorrowRequest.getDatereturn());
                bookBorrow.setBook(bookOpt.get());
                bookBorrow.setUser(userOpt.get());
                bookBorrow.setIsreturn(bookBorrowRequest.isIsreturn());

                bookBorrowRepository.save(bookBorrow);
                return ResponseEntity.ok(Map.of("message", "Borrowed book updated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Borrowed book not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<?> deleteBookBorrow(@PathVariable int id, @RequestHeader("Authorization") String token) {
        try {
            if (!authService.roleAuthAdminLibrarian(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            if (!bookBorrowRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Borrowed book not found"));
            }

            bookBorrowRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Borrowed book deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }
}
