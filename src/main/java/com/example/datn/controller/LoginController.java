package com.example.datn.controller;

import com.example.datn.entity.DiaChi;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.DiaChiRepository;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Controller
public class LoginController {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KhachHangRepo khachHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private DiaChiRepository diaChiRepository;

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

    @GetMapping("/403")
    public String KhongCoQuyen(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.stream().map(GrantedAuthority::getAuthority).findFirst().orElse("Unknown");
        model.addAttribute("role", role);
        return "/admin/403";
    }

    // Đăng ký
    @GetMapping("/register")
    public String showRegisterForm() {
        return "/admin/register";
    }

    @PostMapping("/register")
    public String add(@ModelAttribute("khachHang") KhachHang khachHang,
                      @RequestParam("tinhThanhPho") String tinhThanhPho,
                      @RequestParam("quanHuyen") String quanHuyen,
                      @RequestParam("xaPhuong") String xaPhuong,
                      @RequestParam("soNhaNgoDuong") String soNhaNgoDuong,
                      Model model) {

        // Kiểm tra email đã tồn tại
        if (khachHangRepository.findByEmail(khachHang.getEmail()).isPresent() ||
                nhanVienRepository.findByEmail(khachHang.getEmail()).isPresent()) {
            model.addAttribute("error", "Email đã tồn tại trong hệ thống!"); // Thêm thông báo lỗi vào model
            return "/admin/register"; // Trả về trang đăng ký
        }

        // Thêm khách hàng và địa chỉ
        LocalDate currentDate = LocalDate.now();
        Date sqlDate = Date.valueOf(currentDate);
        khachHang.setNgayTao(sqlDate);
        khachHang.setTrangThai(true);
        khachHangRepository.save(khachHang);

        DiaChi diaChi = new DiaChi();
        diaChi.setKhachHang(khachHang);
        diaChi.setTinhThanhPho(tinhThanhPho);
        diaChi.setQuanHuyen(quanHuyen);
        diaChi.setXaPhuong(xaPhuong);
        diaChi.setSoNhaNgoDuong(soNhaNgoDuong);
        diaChi.setTrangThai(true);
        diaChiRepository.save(diaChi);

        // Gửi email thông báo
        emailService.sendEmail(
                khachHang.getEmail(),
                "Tạo tài khoản thành công",
                khachHang.getEmail(),
                khachHang.getMatKhau(),
                "http://yourwebsite.com"
        );

        model.addAttribute("success", "Tạo tài khoản thành công, vui lòng kiểm tra email.");
        return "redirect:/login"; // Chuyển đến trang login
    }

    @GetMapping("forgot-password")
    public String showForgotPasswordForm() {
        return "/admin/forgot-password";
    }



    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        System.out.println("Received request to reset password for email: " + email);
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByEmail(email);
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByEmail(email);

        if (!khachHangOpt.isPresent() && !nhanVienOpt.isPresent()) {
            model.addAttribute("message", "Email không tồn tại!");
            return "/admin/forgot-password";
        }
        String resetToken = UUID.randomUUID().toString();

        if (khachHangOpt.isPresent()) {
            KhachHang khachHang = khachHangOpt.get();
            khachHang.setResetToken(resetToken);
            khachHangRepository.save(khachHang);
        }
        if (nhanVienOpt.isPresent()) {
            NhanVien nhanVien = nhanVienOpt.get();
            nhanVien.setResetToken(resetToken);
            nhanVienRepository.save(nhanVien);
        }

        String resetUrl = "http://localhost:8080/reset-password?token=" + resetToken;
        emailService.sendEmailQuenMatKhau(email,
                "Đặt lại mật khẩu",
                "Nhấn vào liên kết sau để đặt lại mật khẩu: " + resetUrl);

        model.addAttribute("message", "Vui lòng kiểm tra email để đặt lại mật khẩu.");
        return "/admin/forgot-password";
    }


    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "/admin/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword, Model model) {
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByResetToken(token);
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByResetToken(token);

        // Kiểm tra token hợp lệ cho cả KhachHang và NhanVien
        if (!khachHangOpt.isPresent() && !nhanVienOpt.isPresent()) {
            model.addAttribute("message", "Token không hợp lệ!");
            return "/admin/reset-password";
        }

        // Nếu KhachHang tồn tại, cập nhật mật khẩu
        if (khachHangOpt.isPresent()) {
            KhachHang khachHang = khachHangOpt.get();
            khachHang.setMatKhau(newPassword);
            khachHang.setResetToken(null);
            khachHangRepository.save(khachHang);
        }

        // Nếu NhanVien tồn tại, cập nhật mật khẩu
        if (nhanVienOpt.isPresent()) {
            NhanVien nhanVien = nhanVienOpt.get();
            nhanVien.setMatKhau(newPassword);
            nhanVien.setResetToken(null);
            nhanVienRepository.save(nhanVien);
        }

        return "redirect:/login";
    }
}



