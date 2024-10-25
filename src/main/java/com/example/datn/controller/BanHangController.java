package com.example.datn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ban-hang")
public class BanHangController {
    @GetMapping("/index")
    public String trangBan() {
        return "admin/ban_hang_tai_quay/index";
    }
}
