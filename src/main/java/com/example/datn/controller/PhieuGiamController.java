package com.example.datn.controller;

import com.example.datn.entity.PhieuGiam;
import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.PhieuGiamRepo;
import com.example.datn.repository.PhieuGiamSanPhamRepo;
import com.example.datn.repository.SanPhamRepo;
import com.example.datn.service.PhieuGiamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/phieu-giam")
@RequiredArgsConstructor
public class PhieuGiamController {

    private final PhieuGiamRepo pggRepo;
    private final PhieuGiamService pggSV;
    private final SanPhamRepo spRepo;

    //phieu giam gia
    @GetMapping("/index")
    public String phieuGiam(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) Boolean status
    ) {
        if (page < 0) {
            page = 0;
        }
        PageRequest pageable = PageRequest.of(page, size);
        String searchTerm = "%" + keyword + "%";

        // Xây dựng điều kiện tìm kiếm
        Page<PhieuGiam> phieuGiamGiaPage;
        if (status != null) {
            phieuGiamGiaPage = pggRepo.findByTenLikeAndTrangThai(searchTerm, status, pageable);
        } else {
            phieuGiamGiaPage = pggRepo.findByTenLike(searchTerm, pageable);
        }

        model.addAttribute("ListPGG", phieuGiamGiaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", phieuGiamGiaPage.getTotalPages());
        model.addAttribute("pgg", new PhieuGiam());

        return "admin/phieu_giam/index";
    }

    @GetMapping("/store")
    public String store(Model model) {
        // Lấy danh sách sản phẩm từ repository
        List<SanPham> sanPhams = spRepo.findAll();
        // Thêm danh sách sản phẩm vào model
        model.addAttribute("sanPhams", sanPhams);
        return "/admin/phieu_giam/create";
    }



//    @GetMapping("/discount")
//    public String test(Model model) {
//        List<SanPhamChiTiet> sanPhamChiTiets = pggRepo.findByPhieuGiamNotNull();
//        model.addAttribute("sanPhamChiTiets", sanPhamChiTiets);
//        return "/admin/phieu_giam/cart"; // Trả về view test
//    }

    @PostMapping("/add")
    public String add(PhieuGiam pgg, @RequestParam("sanPhamId") Long sanPhamId) {
        LocalDate currentDate = LocalDate.now();
        java.sql.Date sqlDate = Date.valueOf(currentDate);
        pgg.setNgayTao(sqlDate);
        pgg.setNguoiTao("admin");
        // Tự động tạo mã cho phiếu giảm giá
        String ma = pggSV.taoMaTuDong();
        pgg.setMa(ma);
        // Lấy sản phẩm từ ID
        SanPham sanPham = spRepo.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        // Gán sản phẩm vào phiếu giảm giá
        pgg.setSanPham(sanPham);
        // Lưu phiếu giảm giá vào cơ sở dữ liệu
        PhieuGiam savedPgg = pggRepo.save(pgg);
        // Thêm thông tin vào bảng sản phẩm
        SanPham updatedSanPham = sanPham; // Cập nhật thông tin sản phẩm nếu cần
        spRepo.save(updatedSanPham); // Lưu lại sản phẩm với thông tin đã cập nhật

        return "redirect:/admin/phieu-giam/index";
    }



    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        PhieuGiam phieuGiam = pggRepo.findById(id).orElse(null);
        model.addAttribute("pgg", phieuGiam);
        return "admin/phieu_giam/update";
    }

    @PostMapping("/update")
    public String update(PhieuGiam phieuGiam) {
        LocalDate currentDate = LocalDate.now(); // Lấy ngày hiện tại
        java.sql.Date sqlDate = java.sql.Date.valueOf(currentDate); // Chuyển đổi LocalDate sang Date
        phieuGiam.setNgaySua(sqlDate);
        pggRepo.save(phieuGiam);
        return "redirect:/admin/phieu-giam/index";
    }
}
