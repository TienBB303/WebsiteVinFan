package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.thuoc_tinh.KieuQuat;
import com.example.datn.repository.ThuocTinhRepo.KieuQuatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/thuoc-tinh/kieu-quat")
public class KieuQuatController {
    @Autowired
    KieuQuatRepo kieuQuatRepo;

    @ModelAttribute("listKieuQuat")
    public List<KieuQuat> listKieuQuat() {
        return kieuQuatRepo.findAll();
    }

    @GetMapping("/view")
    public String chuyenView(Model model){
        return "admin/thuoc_tinh/kieu_quat";
    }
}
