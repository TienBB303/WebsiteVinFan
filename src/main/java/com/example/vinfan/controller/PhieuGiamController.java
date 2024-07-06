package com.example.vinfan.controller;

import com.example.vinfan.entity.PhieuGiam;
import com.example.vinfan.repository.PhieuGiamRepo;
import com.example.vinfan.service.PhieuGiamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Date;

@Controller
@RequestMapping("/admin/phieu-giam")
public class PhieuGiamController {

    @Autowired
    PhieuGiamRepo pggRepo;
    @Autowired
    PhieuGiamService pggSV;

    //phieu giam gia
    @GetMapping("/index")
    public String phieuGiam(Model model) {
        model.addAttribute("ListPGG", pggRepo.findAll());
        return "/admin/phieu_giam/index";
    }
    @PostMapping("/add")
    public String addPGG(
            Model model,
            @RequestParam("ten") String ten,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam("loaiPhieuGiam") boolean loaiPhieu,
            @RequestParam("giaTriMin") BigDecimal min,
            @RequestParam("giaTriMax") BigDecimal max,
            @RequestParam("nguoiTao") String nguoiTao,
            @RequestParam("nguoiSua") String nguoiSua,
            @RequestParam("trangThai") boolean trangThai
    ){
        String ma = pggSV.taoMaTuDong();  // Tạo mã sản phẩm bằng tự động

        PhieuGiam sanPham = new PhieuGiam();
        sanPham.setMa(ma);
        sanPham.setTen(ten);
        sanPham.setNgayBD(new Date());  // Set ngày tạo bằng ngày hiện tại
        sanPham.setNgayKT(new Date());
        sanPham.setSoLuong(soLuong);
        sanPham.setLoaiPhieuGiam(loaiPhieu);
        sanPham.setGiaTriMax(max);
        sanPham.setGiaTriMin(min);
        sanPham.setNgayTao(new Date());
        sanPham.setNgaySua(new Date());
        sanPham.setNguoiTao(nguoiTao);
        sanPham.setNguoiSua(nguoiSua);
        sanPham.setTrangThai(trangThai);

        pggRepo.save(sanPham);
        return "redirect:/admin/phieu-giam/index";
    }
}
