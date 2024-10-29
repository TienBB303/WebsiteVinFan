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
    public String login() {
        return "/admin/login";
    }

}
