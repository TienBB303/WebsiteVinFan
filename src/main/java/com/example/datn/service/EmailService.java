package com.example.datn.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String username, String password, String linkweb) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("famumintouan@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText("Tên tài khoản: " + username + "\n" +
                "Mật khẩu: " + password + "\n" +
                "Đô nết cho mình nhé: " + linkweb);

        mailSender.send(message);
    }
    public void sendEmailQuenMatKhau(String to, String subject,String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("famumintouan@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

}
