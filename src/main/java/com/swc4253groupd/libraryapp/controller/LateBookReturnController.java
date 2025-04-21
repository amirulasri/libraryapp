package com.swc4253groupd.libraryapp.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swc4253groupd.libraryapp.model.BookBorrow;
import com.swc4253groupd.libraryapp.notification.EmailService;
import com.swc4253groupd.libraryapp.repository.BookBorrowRepository;
import com.swc4253groupd.libraryapp.service.AuthService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/bookborrows/latereturn")
public class LateBookReturnController {
    @Autowired
    private BookBorrowRepository bookBorrowRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    private ResponseEntity<?> getLateReturn(@RequestHeader("Authorization") String token) {
        try {
            if (!authService.roleAuthAdminLibrarian(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            LocalDate now = LocalDate.now();

            List<BookBorrow> lateBorrows = bookBorrowRepository.findByDatereturnBeforeAndIsreturnFalse(now);

            List<Map<String, Object>> resultWithFine = lateBorrows.stream().map(bb -> {
                long daysLate = ChronoUnit.DAYS.between(bb.getDatereturn(), now);
                int fine = calculateFine(daysLate);

                return Map.of(
                        "bookborrowid", bb.getBookborrowid(),
                        "dateborrowed", bb.getDateborrowed(),
                        "datereturn", bb.getDatereturn(),
                        "isreturn", bb.getIsreturn(),
                        "fine", fine,
                        "book", bb.getBook(),
                        "user", bb.getUser());
            }).collect(Collectors.toList());

            return ResponseEntity.ok(resultWithFine);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    //PART AIN BATRISYIA HERE - 1
    @PostMapping
    public ResponseEntity<?> sendEmailToAllLateUsers(@RequestHeader("Authorization") String token) {
        try {
            if (!authService.roleAuthAdminLibrarian(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            LocalDate now = LocalDate.now();
            List<BookBorrow> lateBorrows = bookBorrowRepository.findByDatereturnBeforeAndIsreturnFalse(now);

            for (BookBorrow borrow : lateBorrows) {
                long daysLate = ChronoUnit.DAYS.between(borrow.getDatereturn(), now);
                int fineAmount = calculateFine(daysLate);

                try {
                    emailService.sendLateReturnReminder(
                            borrow.getUser().getEmail(),
                            borrow.getBook().getTitle(),
                            borrow.getBook().getAuthor(),
                            borrow.getDatereturn().toString(),
                            fineAmount);
                } catch (MessagingException e) {
                    System.err.println("Error sending email: " + e.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of("message", "Late return emails sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired token"));
        }
    }

    // PART DANIEL LUQMAN HERE
    private int calculateFine(long daysLate) {
        int fine = (int) (daysLate * 1);
        return Math.min(fine, 30);
    }
}
