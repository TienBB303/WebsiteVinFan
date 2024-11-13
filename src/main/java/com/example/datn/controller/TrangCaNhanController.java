package com.example.datn.controller;

import com.example.datn.entity.ChucVu;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.ChucVuRepository;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/trang-ca-nhan")
public class TrangCaNhanController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ChucVuRepository chucVuRepository;

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
        return "/admin/thong-tin-ca-nhan/thong-tin-nhan-vien";
    }
    @GetMapping("/from-sua-nhan-vien/{id}")
    public String formSuaNhanVien(@PathVariable("id") Integer id, Model model) {
        NhanVien nhanVien = nhanVienRepository.findById(id).orElse(null);
        List<ChucVu> chucVuList = chucVuRepository.findAll();
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("chucVuList", chucVuList);
        model.addAttribute("nowday", LocalDate.now());
        return "/admin/thong-tin-ca-nhan/sua-nhan-vien";
    }
    @PostMapping("/update")
    public String suaNhanVien(NhanVien nhanVien,@RequestParam("file") MultipartFile file) throws IOException {

        Map uploadResult = cloudinaryService.upload(file);
        String imageUrl =(String) uploadResult.get("url");
        nhanVien.setHinhAnh(imageUrl);

        LocalDate currentDate = LocalDate.now();
        Date sqlDate = Date.valueOf(currentDate);
        nhanVien.setNgaySua(sqlDate);

        if (nhanVien.getGioiTinh() == null) {
            nhanVien.setGioiTinh(false); // Hoặc một giá trị mặc định khác
        }
        nhanVienRepository.save(nhanVien);
        return "redirect:/trang-ca-nhan/index";
    }

}