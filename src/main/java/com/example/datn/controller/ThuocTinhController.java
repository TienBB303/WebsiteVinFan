package com.example.datn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class ThuocTinhController {
    @GetMapping("/thuoc-tinh")
    public String hienThiThuocTinh(){
        return "redirect:/admin/san_pham/thuoc_tinh";
    }
}
