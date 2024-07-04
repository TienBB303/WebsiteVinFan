package com.example.vinfan.controller;

import com.example.vinfan.repository.SPCTRepo;
import com.example.vinfan.repository.SanPhamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    //    Repository mapping
    @Autowired
    SanPhamRepo sanPhamRepo;
    @Autowired
    SPCTRepo spctRepo;


    //  Mapping Admin
    @GetMapping("/index")
    public String admin() {
        return "/admin/index";
    }


    //    SanPham
    @GetMapping("/san-pham")
    public String sanPhamHienThi(Model model) {
        model.addAttribute("list", sanPhamRepo.findAll());
        return "/admin/san_pham/index";
    }
    @GetMapping("/san-pham-chi-tiet")
    public String sanPhamChiTietHienThi(Model model) {
//        model.addAttribute("list", spctRepo.findAll());
        return "/admin/san_pham/san_pham_chi_tiet";
    }
}
