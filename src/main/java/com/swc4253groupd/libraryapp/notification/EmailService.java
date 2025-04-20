package com.swc4253groupd.libraryapp.notification;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.swc4253groupd.libraryapp.model.BookBorrow;
import com.swc4253groupd.libraryapp.repository.BookBorrowRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BookBorrowRepository bookBorrowRepository;

    public void sendEmailAfterBook(String to, String subject, String bookTitle, String author, String dateBorrowed,
            String datereturn) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String htmlContent = """
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            padding: 20px;
                        }
                        .container {
                            background-color: #ffffff;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
                        }
                        h2 {
                            color: #2c3e50;
                            text-align: center;
                        }
                        p {
                            font-size: 16px;
                            color: #555;
                        }
                        .book-details {
                            margin-top: 20px;
                            padding: 10px;
                            border: 1px solid #ddd;
                            border-radius: 5px;
                            background-color: #f9f9f9;
                        }
                        .footer {
                            margin-top: 20px;
                            font-size: 14px;
                            text-align: center;
                            color: #777;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h2>📚 Thank You for Borrowing Our Books!</h2>
                        <p>Please return the book before the due date to avoid penalties.</p>

                        <div class="book-details">
                            <p><strong>📖 Book Title:</strong> %s</p>
                            <p><strong>✍️ Author:</strong> %s</p>
                            <p><strong>📅 Date Borrowed:</strong> %s</p>
                            <p><strong>⏳ Due Date:</strong> %s</p>
                        </div>

                        <p>We appreciate your support for the library!</p>

                        <div class="footer">
                            <p>📩 Contact us: Library App For SWC4253 | By Group D</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(bookTitle, author, dateBorrowed, datereturn);

        message.setFrom("amirulasrixserver@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom("amirulasrixserver@gmail.com");

        mailSender.send(message);
    }

    public void sendLateReturnReminder(String to, String bookTitle, String author, String datereturn, int fine)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String htmlContent = """
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }
                        .container { background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1); }
                        h2 { color: #d63031; text-align: center; }
                        p { font-size: 16px; color: #555; }
                        .book-details { margin-top: 20px; padding: 10px; border: 1px solid #ddd; border-radius: 5px; background-color: #f9f9f9; }
                        .footer { margin-top: 20px; font-size: 14px; text-align: center; color: #777; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h2>⚠️ Late Book Return Reminder!</h2>
                        <p>Your borrowed book is overdue. Please return it immediately to avoid higher penalties.</p>

                        <div class="book-details">
                            <p><strong>📖 Book Title:</strong> %s</p>
                            <p><strong>✍️ Author:</strong> %s</p>
                            <p><strong>⏳ Due Date:</strong> %s</p>
                            <p><strong>💰 Fine Amount:</strong> RM %d</p>
                        </div>

                        <p>Failure to return the book may result in additional fines.</p>

                        <div class="footer">
                            <p>📩 Contact us: Library App For SWC4253 | By Group D</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(bookTitle, author, datereturn, fine);

        helper.setTo(to);
        helper.setSubject("Library Late Return Notice");
        helper.setText(htmlContent, true);
        helper.setFrom("amirulasrixserver@gmail.com");

        mailSender.send(message);
    }

    @Scheduled(cron = "0 0 8 */3 * ?") // Runs every 3 days at 8 AM
    public void sendEmailLateBook() {
        LocalDate now = LocalDate.now();

        List<BookBorrow> lateBorrows = bookBorrowRepository.findByDatereturnBeforeAndIsreturnFalse(now);
        for (BookBorrow borrow : lateBorrows) {
            long daysLate = ChronoUnit.DAYS.between(borrow.getDatereturn(), now);
            int fineAmount = calculateFine(daysLate);

            if (fineAmount < 30 && borrow.getNotificationCount() < (30 / 0.5)) { // Stop emails if fine reaches 30
                try {
                    sendLateReturnReminder(
                            borrow.getUser().getEmail(),
                            borrow.getBook().getTitle(),
                            borrow.getBook().getAuthor(),
                            borrow.getDatereturn().toString(),
                            fineAmount);

                    borrow.setNotificationCount(borrow.getNotificationCount() + 1);
                    bookBorrowRepository.save(borrow); // Update count in DB
                } catch (MessagingException e) {
                    System.err.println("Error sending email: " + e.getMessage());
                }
            }
        }
    }

    private int calculateFine(long daysLate) {
        int fine = (int) (daysLate * 0.5);
        return Math.min(fine, 30);
    }
}