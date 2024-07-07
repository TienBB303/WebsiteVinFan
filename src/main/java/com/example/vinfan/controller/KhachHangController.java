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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Controller
@RequestMapping("/admin/khach-hang/")
public class KhachHangController {

    @Autowired
    private KhachHangRepo khachHangRepo;

    @GetMapping("hien-thi")
    public String loadTable(Model model) {
        model.addAttribute("listsKhachhang", khachHangRepo.findAll());
        return "/khach-hang/index";
    }
    @GetMapping("from-them")
    public String fromThem(Model model) {
        LocalDate nowday = LocalDate.now();
        model.addAttribute("nowday",nowday);
        return "/khach-hang/add";
    }

    @PostMapping("add")
    public String add(KhachHang khachHang){
        khachHangRepo.save(khachHang);
        return "redirect:/admin/khach-hang/hien-thi";
    }

    @GetMapping("from-sua/{id}")
    public String fromSua(Model model, @PathVariable("id") Integer id) {
        LocalDate nowday = LocalDate.now();
        model.addAttribute("khachHang",khachHangRepo.findById(id).orElse(null));
        model.addAttribute("nowday",nowday);
        return "/khach-hang/sua";
    }

    @PostMapping("update")
    public String sua(KhachHang khachHang,Integer id){
        khachHang.setId(id);
        khachHangRepo.save(khachHang);
        return "redirect:/admin/khach-hang/hien-thi";
    }

}
