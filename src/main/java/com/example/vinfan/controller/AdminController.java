package com.example.vinfan.controller;


import com.example.vinfan.repository.PhieuGiamRepo;
import com.example.vinfan.entity.SanPham;
import com.example.vinfan.repository.SPCTRepo;
import com.example.vinfan.repository.SanPhamRepo;
import com.example.vinfan.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    SanPhamRepo sanPhamRepo;
    @Autowired
    SPCTRepo spctRepo;
    @Autowired
    PhieuGiamRepo pggRepo;
    @Autowired
    SanPhamService sanPhamService;

    @GetMapping("/index")
    public String admin() {
        return "/admin/index";
    }

    @GetMapping("/san-pham")
    public String sanPhamHienThi(Model model) {
        model.addAttribute("list", sanPhamRepo.findAll());
        return "/admin/san_pham/index";
    }

    @PostMapping("/san-pham/add")
    public String addSanPham(
            @RequestParam("ten") String ten,
            @RequestParam("mo_ta") String moTa,
            @RequestParam("trang_thai") Boolean trangThai
    ) {
        String ma = sanPhamService.taoMaTuDong();  // Tạo mã sản phẩm bằng tự động

        SanPham sanPham = new SanPham();
        sanPham.setMa(ma);
        sanPham.setTen(ten);
        sanPham.setMo_ta(moTa);
        sanPham.setNgay_tao(new Date());  // Set ngày tạo bằng ngày hiện tại
        sanPham.setTrang_thai(trangThai);
        sanPham.setNgay_sua(new Date());  // Set ngày sửa bằng ngày hiện tại

        sanPhamRepo.save(sanPham);
        return "redirect:/admin/san-pham";
    }
  
    @GetMapping("/san-pham-chi-tiet")
    public String sanPhamChiTietHienThi(Model model) {
        // model.addAttribute("list", spctRepo.findAll());
        return "/admin/san_pham/san_pham_chi_tiet";
    }

    //phieu giam gia
    @GetMapping("/phieu-giam")
    public String phieuGiam(Model model) {
        model.addAttribute("ListPGG", pggRepo.findAll());
        return "/admin/phieu_giam/index";
    }
}
