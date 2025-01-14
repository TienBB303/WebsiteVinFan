package com.example.datn.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("famumintouan@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText("Tên tài khoản: " + username + "\n" +
                "Mật khẩu: " + password);

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

    public void sendEmailChuyenDoiQuyen(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("famumintouan@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendEmailDangKiTaiKhoanThanhCong(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("famumintouan@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

}
