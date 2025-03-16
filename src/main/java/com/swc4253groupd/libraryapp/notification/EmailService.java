package com.swc4253groupd.libraryapp.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String bookTitle, String author, String dateBorrowed,
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
}