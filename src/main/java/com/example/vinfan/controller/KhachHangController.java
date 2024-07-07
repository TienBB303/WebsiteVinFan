package com.example.vinfan.controller;


import com.example.vinfan.entity.KhachHangEntity.KhachHang;
import com.example.vinfan.repository.KhachHangRepo;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/khach-hang/")
public class KhachHangController {

    @Autowired
    private KhachHangRepo khachHangRepo;

    @GetMapping("index")
    public String loadTable(Model model) {
        model.addAttribute("listsKhachhang", khachHangRepo.findAll());
        return "/admin/khach-hang/index";
    }
    @GetMapping("from-them")
    public String fromThem(Model model) {
        return "/admin/khach-hang/add";
    }

    @PostMapping("add")
    public String add(KhachHang khachHang){
        LocalDate currentDate = LocalDate.now(); // Lấy ngày hiện tại
        java.sql.Date sqlDate = Date.valueOf(currentDate); // Chuyển đổi LocalDate sang Date
        khachHang.setNgayTao(sqlDate);
        khachHangRepo.save(khachHang);
        return "redirect:/admin/khach-hang/index";
    }

    @GetMapping("from-sua/{id}")
    public String fromSua(Model model, @PathVariable("id") Integer id) {

        model.addAttribute("khachHang",khachHangRepo.findById(id).orElse(null));
        return "/admin/khach-hang/sua";
    }

    @PostMapping("update")
    public String sua(KhachHang khachHang,Integer id){
        LocalDate currentDate = LocalDate.now(); // Lấy ngày hiện tại
        java.sql.Date sqlDate = Date.valueOf(currentDate); // Chuyển đổi LocalDate sang Date
        khachHang.setNgaySua(sqlDate);
//        khachHang.setId(id);
        khachHangRepo.save(khachHang);
        return "redirect:/admin/khach-hang/index";
    }

}
