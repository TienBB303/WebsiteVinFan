package com.example.vinfan.controller;


import com.example.vinfan.entity.ChucVu;
import com.example.vinfan.entity.NhanVien;
import com.example.vinfan.repository.ChucVuRepository;
import com.example.vinfan.repository.NhanVienRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.sql.Date;
import java.time.LocalDate;


import java.util.List;

@Controller
@RequestMapping("/admin/nhan-vien/")
public class NhanVienController {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private ChucVuRepository chucVuRepository;

    @GetMapping("/hien-thi")
    public String hienThiNhanVien(Model model) {
        List<NhanVien> nhanVienList = nhanVienRepository.findAll();
        model.addAttribute("listsNhanVien", nhanVienList);
        return "admin/nhan-vien/index";
    }

    @GetMapping("/from-them")
    public String formThemNhanVien(Model model) {
        NhanVien nhanVien = new NhanVien();
        List<ChucVu> chucVuList = chucVuRepository.findAll();
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("chucVuList", chucVuList);
        return "admin/nhan-vien/add";
    }

    @PostMapping("/add")
    public String addNhanVien(NhanVien nhanVien) {
        LocalDate currentDate = LocalDate.now(); // Lấy ngày hiện tại
        Date sqlDate = Date.valueOf(currentDate); // Chuyển đổi LocalDate sang Date
        nhanVien.setNgayTao(sqlDate); // Lưu ngày hiện tại vào đối tượng NhanVien
        nhanVienRepository.save(nhanVien);
        return "redirect:/admin/nhan-vien/hien-thi";
    }

    @GetMapping("/from-sua/{id}")
    public String formSuaNhanVien(@PathVariable("id") Integer id, Model model) {
        NhanVien nhanVien = nhanVienRepository.findById(id).orElse(null);
        List<ChucVu> chucVuList = chucVuRepository.findAll();
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("chucVuList", chucVuList);
        model.addAttribute("nowday", LocalDate.now());
        return "admin/nhan-vien/sua";
    }
    @PostMapping("/update")
    public String suaNhanVien(NhanVien nhanVien) {
        LocalDate currentDate = LocalDate.now(); // Lấy ngày hiện tại
        Date sqlDate = Date.valueOf(currentDate); // Chuyển đổi LocalDate sang Date
        nhanVien.setNgaySua(sqlDate);
        nhanVienRepository.save(nhanVien);
        return "redirect:/admin/nhan-vien/hien-thi";
    }

}
