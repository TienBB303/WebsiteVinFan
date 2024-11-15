package com.example.datn.controller;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.NhanVienRepository;
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



}
