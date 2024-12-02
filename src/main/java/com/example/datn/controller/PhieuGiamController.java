package com.example.datn.controller;

import com.example.datn.entity.PhieuGiam;
import com.example.datn.entity.PhieuGiamSanPham;
import com.example.datn.entity.san_pham.SanPham;
import com.example.datn.entity.san_pham.SanPhamChiTiet;
import com.example.datn.repository.PhieuGiamRepo;
import com.example.datn.repository.PhieuGiamSanPhamRepo;
import com.example.datn.repository.san_pham_repo.SPCTRepo;
import com.example.datn.repository.san_pham_repo.SanPhamRepo;
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
    public String add(@ModelAttribute PhieuGiam pgg, @RequestParam("sanPhamId") Long sanPhamId) {
        LocalDate currentDate = LocalDate.now();
        pgg.setNgayTao(Date.valueOf(currentDate));
        pgg.setNguoiTao("admin");

        // Tự động tạo mã cho phiếu giảm giá
        String ma = pggSV.taoMaTuDong();
        pgg.setMa(ma);

        // Lấy sản phẩm từ ID
        SanPham sanPham = spRepo.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + sanPhamId));
        pgg.setSanPham(sanPham); // Gán sản phẩm cho phiếu giảm giá

        // Lưu phiếu giảm giá vào cơ sở dữ liệu
        PhieuGiam savedPgg = pggRepo.save(pgg);

        // Lưu thông tin phiếu giảm giá - sản phẩm vào bảng trung gian
        PhieuGiamSanPham pggSanPham = new PhieuGiamSanPham();
        pggSanPham.setPhieuGiam(savedPgg);
        pggSanPham.setSanPham(sanPham);

        // Nếu có sản phẩm chi tiết
        List<SanPhamChiTiet> sanPhamChiTietList = spctRepo.findBySanPhamId(sanPhamId);
        if (!sanPhamChiTietList.isEmpty()) {
            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietList.get(0); // Lấy sản phẩm chi tiết đầu tiên
            pggSanPham.setSanPhamChiTiet(sanPhamChiTiet);
            pggSanPham.setGiaSauGiam(sanPhamChiTiet.getGia().subtract(savedPgg.getGiaTriGiam()));
        }

        pggspRepo.save(pggSanPham); // Lưu vào bảng trung gian

        return "redirect:/admin/phieu-giam/index";
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        PhieuGiam phieuGiam = pggRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu giảm giá không tồn tại với ID: " + id));

        SanPham sanPham = phieuGiam.getSanPham(); // Lấy sản phẩm liên kết (có thể null)
        model.addAttribute("pgg", phieuGiam);
        model.addAttribute("sanPham", sanPham);

        // Truyền trạng thái chỉnh sửa để kiểm soát giao diện
        boolean isEditable = phieuGiam.isTrangThai();
        model.addAttribute("isEditable", isEditable);

        return "admin/phieu_giam/update"; // Luôn hiển thị trang update
    }

    @PostMapping("/update")
    public String update(@ModelAttribute PhieuGiam phieuGiam) {
        LocalDate currentDate = LocalDate.now();
        phieuGiam.setNgaySua(Date.valueOf(currentDate));

        if (!phieuGiam.isTrangThai()) {
            // Nếu trạng thái là "Ngừng áp dụng", xóa liên kết sản phẩm
            List<PhieuGiamSanPham> links = pggspRepo.findByPhieuGiamId(phieuGiam.getId());
            if (!links.isEmpty()) {
                pggspRepo.deleteAll(links); // Xóa liên kết
            }
        } else {
            // Nếu trạng thái là "Áp dụng lại", tái tạo liên kết nếu cần
            List<PhieuGiamSanPham> links = pggspRepo.findByPhieuGiamId(phieuGiam.getId());
            if (links.isEmpty()) {
                SanPham sanPham = phieuGiam.getSanPham();
                SanPhamChiTiet sanPhamChiTiet = spctRepo.findBySanPhamId(sanPham.getId()).get(0);

                PhieuGiamSanPham newLink = new PhieuGiamSanPham();
                newLink.setPhieuGiam(phieuGiam);
                newLink.setSanPham(sanPham);
                newLink.setSanPhamChiTiet(sanPhamChiTiet);
                newLink.setGiaSauGiam(sanPhamChiTiet.getGia().subtract(phieuGiam.getGiaTriGiam()));

                pggspRepo.save(newLink); // Lưu liên kết mới
            }
        }

        // Lưu thay đổi trạng thái của phiếu giảm giá
        pggRepo.save(phieuGiam);
        return "redirect:/admin/phieu-giam/index";
    }
}
