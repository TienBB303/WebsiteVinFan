package com.example.datn.controller;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.SPCTRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/ban-hang")
public class BanHangController {

    @Autowired
    private SPCTRepo spctRepo;

    @GetMapping("/index")
    public String trangBan(Model model) {
        List<SanPhamChiTiet> danhSachSanPham = spctRepo.findAll();
        System.out.println("Danh sách sản phẩm: " + danhSachSanPham); // Debug line
        model.addAttribute("danhSachSanPham", danhSachSanPham);
        return "admin/ban_hang_tai_quay/index";
    }

//    @PostMapping("/thanh-toan")
//    public String thanhToan(@RequestParam("idSP") String id,
//                            @RequestParam("tenSP") String ten,
//                            @RequestParam("soLuongSP") Integer soLuong){
////        SanPhamChiTiet spct = spctRepo.findById(id).orElse(null);
//        return "admin/ban_hang_tai_quay/index";
//    }

//    @GetMapping("/tim-kiem")
//    public String timKiem(){
//
//    }

}
