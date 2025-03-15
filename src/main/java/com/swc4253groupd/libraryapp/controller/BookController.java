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

import com.swc4253groupd.libraryapp.dto.BookRequestDTO;
import com.swc4253groupd.libraryapp.model.Book;
import com.swc4253groupd.libraryapp.repository.BookRepository;
import com.swc4253groupd.libraryapp.security.JwtUtil;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getBooks(@RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

            if ((!"ADMIN".equals(role)) && (!"LIBRARIAN".equals(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            List<Book> books = (List<Book>) bookRepository.findAll();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable int id, @RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

            if ((!"ADMIN".equals(role)) && (!"LIBRARIAN".equals(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            Optional<Book> book = bookRepository.findById(id);
            if (book.isPresent()) {
                return ResponseEntity.ok(book.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Book not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    @PostMapping
    public ResponseEntity<?> registerBook(@RequestBody BookRequestDTO bookRequest,
            @RequestHeader("Authorization") String token) {

        try {
            String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

            if ((!"ADMIN".equals(role)) && (!"LIBRARIAN".equals(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            Book book = new Book();
            book.setTitle(bookRequest.getTitle());
            book.setAuthor(bookRequest.getAuthor());
            book.setYearpublished(bookRequest.getYearpublished());

            Book savedBook = bookRepository.save(book);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "id", savedBook.getBookid(),
                            "title", savedBook.getTitle(),
                            "author", savedBook.getAuthor(),
                            "yearpublished", savedBook.getYearpublished()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable int id, @RequestBody BookRequestDTO bookRequest,
            @RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

            if ((!"ADMIN".equals(role)) && (!"LIBRARIAN".equals(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            Optional<Book> existingbook = bookRepository.findById(id);
            if (existingbook.isPresent()) {
                Book book = existingbook.get();
                book.setTitle(bookRequest.getTitle());
                book.setAuthor(bookRequest.getAuthor());
                book.setYearpublished(bookRequest.getYearpublished());

                bookRepository.save(book);
                return ResponseEntity.ok(Map.of("message", "Book updated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Book not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable int id, @RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtil.extractRole(token.replace("Bearer ", ""));

            if ((!"ADMIN".equals(role)) && (!"LIBRARIAN".equals(role))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            if (!bookRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Book not found"));
            }

            bookRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }
}
