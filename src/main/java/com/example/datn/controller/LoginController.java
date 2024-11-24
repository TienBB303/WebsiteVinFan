package com.example.datn.controller;

import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private KhachHangRepo khachHangRepository;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "username", required = false) String username,
                        Model model) {
        if (error != null) {

            model.addAttribute("error", "Tên tài khoản hoặc mật khẩu không đúng.");
        }
        model.addAttribute("username", username);  // Giữ lại username khi nhập sai
        return "/admin/login";
    }
    // Đăng ký
    @GetMapping("/register")
    public String showRegisterForm() {
        return "/admin/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String name,
                               Model model) {
        // Kiểm tra email đã tồn tại
        if (khachHangRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email đã tồn tại!");
            return "/admin/register";
        }

        // Tạo người dùng mới
        KhachHang khachHang = new KhachHang();
        khachHang.setEmail(email);
        khachHang.setMatKhau(password);
        khachHang.setTen(name);
        khachHangRepository.save(khachHang);

        return "redirect:/login";
    }

    // Quên mật khẩu
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "/admin/forgot-password";
    }

//    @PostMapping("/forgot-password")
//    public String forgotPassword(@RequestParam String email, Model model) {
//        // Kiểm tra email
//        Optional<KhachHang> khachHangOpt = khachHangRepository.findByEmail(email);
//        if (!khachHangOpt.isPresent()) {
//            model.addAttribute("message", "Email không tồn tại!");
//            return "/admin/forgot-password";
//        }
//
//        // Tạo mã đặt lại mật khẩu
//        String resetToken = UUID.randomUUID().toString();
//        KhachHang khachHang = khachHangOpt.get();
//        khachHang.setResetToken(resetToken);
//        khachHangRepository.save(khachHang);
//
//        // Gửi email
//        String resetUrl = "http://localhost:8080/reset-password?token=" + resetToken;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Đặt lại mật khẩu");
//        message.setText("Nhấn vào liên kết sau để đặt lại mật khẩu: " + resetUrl);
//        mailSender.send(message);
//
//        model.addAttribute("message", "Vui lòng kiểm tra email để đặt lại mật khẩu.");
//        return "/admin/forgot-password";
//    }
//
//    // Đặt lại mật khẩu
//    @GetMapping("/reset-password")
//    public String showResetPasswordForm(@RequestParam String token, Model model) {
//        model.addAttribute("token", token);
//        return "/admin/reset-password";
//    }
//
//    @PostMapping("/reset-password")
//    public String resetPassword(@RequestParam String token,
//                                @RequestParam String newPassword,
//                                Model model) {
//        Optional<KhachHang> khachHangOpt = khachHangRepository.findByResetToken(token);
//        if (!khachHangOpt.isPresent()) {
//            model.addAttribute("message", "Token không hợp lệ!");
//            return "/admin/reset-password";
//        }
//
//        // Cập nhật mật khẩu mới
//        KhachHang khachHang = khachHangOpt.get();
//        khachHang.setMatKhau(passwordEncoder.encode(newPassword));
//        khachHang.setResetToken(null); // Xóa token
//        khachHangRepository.save(khachHang);
//
//        return "redirect:/login";
//    }




}
