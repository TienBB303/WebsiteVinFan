package com.example.datn.controller;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @GetMapping("/index")
    public String getUserInfo(Model model) {
        NhanVien nhanVien = nhanVienRepository.profileNhanVien();

        if (nhanVien == null) {
            model.addAttribute("errorMessage", "Không tìm thấy thông tin nhân viên.");
            return "/admin/error"; // Điều hướng đến trang lỗi
        }

        model.addAttribute("nhanVien", nhanVien);
        return "/admin/index";
    }

}
