package com.example.datn.controller;




import com.example.datn.entity.ChucVu;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.ChucVuRepository;
import com.example.datn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
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


    @GetMapping("/index")
    public String loadTable(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "trangThai", required = false) Boolean trangThai,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        if (page < 0) {
            page = 0;
        }

        PageRequest pageable = PageRequest.of(page, size);
        Page<NhanVien> nhanVienPage = nhanVienRepository.searchNhanVien(keyword, trangThai, startDate, endDate, pageable);

        model.addAttribute("listsNhanVien", nhanVienPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", nhanVienPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "/admin/nhan-vien/index"; // Đường dẫn đến trang index của nhân viên
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
    public String addNhanVien(@ModelAttribute NhanVien nhanVien) {
        LocalDate currentDate = LocalDate.now(); // Lấy ngày hiện tại
        Date sqlDate = Date.valueOf(currentDate); // Chuyển đổi LocalDate sang Date
        nhanVien.setNgayTao(sqlDate); // Lưu ngày hiện tại vào đối tượng NhanVien
        nhanVien.setTrangThai(true);
        nhanVienRepository.save(nhanVien);
        return "redirect:/admin/nhan-vien/index";
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

        // Kiểm tra và thiết lập các giá trị mặc định nếu cần
        if (nhanVien.getGioiTinh() == null) {
            nhanVien.setGioiTinh(false); // Hoặc một giá trị mặc định khác
        }

        nhanVienRepository.save(nhanVien);
        return "redirect:/admin/nhan-vien/index";
    }


}
