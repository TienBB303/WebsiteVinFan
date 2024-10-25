package com.example.datn.controller;

import com.example.datn.entity.PhieuGiam;
import com.example.datn.entity.PhieuGiamSanPham;
import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.PhieuGiamRepo;
import com.example.datn.repository.PhieuGiamSanPhamRepo;
import com.example.datn.repository.SPCTRepo;
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
    private final PhieuGiamSanPhamRepo pggspRepo;
    private final SPCTRepo spctRepo;

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

    @GetMapping("/discount")
    public String showDiscountProducts(Model model) {
        List<PhieuGiamSanPham> discountProducts = pggspRepo.findAll(); // Truy vấn tất cả thông tin
        model.addAttribute("discountProducts", discountProducts); // Thêm vào model
        return "/admin/phieu_giam/cart"; // Trả về view
    }


    @PostMapping("/add")
    public String add(PhieuGiam pgg, @RequestParam("sanPhamId") Long sanPhamId) {
        LocalDate currentDate = LocalDate.now();
        java.sql.Date sqlDate = Date.valueOf(currentDate);
        pgg.setNgayTao(sqlDate);
        pgg.setNguoiTao("admin");

        // Tự động tạo mã cho phiếu giảm giá
        String ma = pggSV.taoMaTuDong();
        pgg.setMa(ma);

        // Lưu phiếu giảm giá vào cơ sở dữ liệu
        PhieuGiam savedPgg = pggRepo.save(pgg);

        // Lấy sản phẩm từ ID
        SanPham sanPham = spRepo.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        // Lấy danh sách chi tiết sản phẩm từ sản phẩm ID
        List<SanPhamChiTiet> sanPhamChiTietList = spctRepo.findBySanPhamId(sanPhamId);

        // Nếu muốn lấy một sản phẩm chi tiết cụ thể, có thể lấy phần tử đầu tiên trong danh sách
        if (!sanPhamChiTietList.isEmpty()) {
            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietList.get(0); // Lấy chi tiết đầu tiên (tuỳ thuộc logic của bạn)

            // Lưu vào bảng trung gian
            PhieuGiamSanPham pggSanPham = new PhieuGiamSanPham();
            pggSanPham.setSanPhamChiTiet(sanPhamChiTiet); // Gán sản phẩm chi tiết
            pggSanPham.setGiaSauGiam(sanPhamChiTiet.getGia()); // Gán giá sau khi giảm
            pggSanPham.setPhieuGiam(savedPgg); // Gán phiếu giảm giá vừa lưu
            pggSanPham.setSanPham(sanPham); // Gán sản phẩm
            pggspRepo.save(pggSanPham); // Lưu vào bảng trung gian
        }

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
