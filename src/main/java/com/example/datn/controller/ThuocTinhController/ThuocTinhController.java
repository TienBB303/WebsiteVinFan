package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class ThuocTinhController {
    @Autowired
    NhanVienRepository nhanVienRepository;
    @GetMapping("/thuoc-tinh")
    public String hienThiThuocTinh(Model model){
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo", nv);
        return "admin/thuoc_tinh/thuoc_tinh";
    }
}
