package com.example.datn.controller.phieu_giam_gia_controller;

import com.example.datn.entity.phieu_giam.PhieuGiam;
import com.example.datn.entity.phieu_giam.PhieuGiamSanPham;
import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.phieu_giam_repo.PhieuGiamRepo;
import com.example.datn.repository.phieu_giam_repo.PhieuGiamSanPhamRepo;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.repository.SanPhamRepo;
import com.example.datn.service.phieu_giam_service.PhieuGiamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
        List<SanPham> sanPhams = spRepo.findAll();

        // Gắn giá từ `SanPhamChiTiet` vào từng sản phẩm
        sanPhams.forEach(sp -> {
            List<SanPhamChiTiet> chiTietList = spctRepo.findBySanPhamId(sp.getId());
            if (!chiTietList.isEmpty()) {
                sp.setMo_ta("Giá: " + chiTietList.get(0).getGia().toString()); // Gắn giá đầu tiên vào mô tả (hoặc thêm thuộc tính mới)
            } else {
                sp.setMo_ta("Chưa có giá"); // Trường hợp không có giá
            }
        });

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
    public String add(@ModelAttribute PhieuGiam pgg,
                      @RequestParam(value = "sanPhamMa", required = false) String sanPhamMa,
                      Model model) {
        try {
            // Thiết lập thông tin phiếu giảm giá
            pgg.setNgayTao(Date.valueOf(LocalDate.now()));
            pgg.setNguoiTao("admin");
            pgg.setMa(pggSV.taoMaTuDong());

            // Kiểm tra loại phiếu giảm giá
            if (pgg.isLoaiPhieuGiam()) { // Nếu là giảm giá hóa đơn
                sanPhamMa = null; // Không cần mã sản phẩm
            } else {
                // Xử lý nếu là giảm giá sản phẩm
                if (sanPhamMa == null || sanPhamMa.trim().isEmpty()) {
                    throw new IllegalArgumentException("Vui lòng chọn mã sản phẩm!");
                }

                // Lấy danh sách sản phẩm dựa trên mã
                List<SanPham> sanPhamList = spRepo.findByMa(sanPhamMa);
                if (sanPhamList.isEmpty()) {
                    throw new IllegalArgumentException("Mã sản phẩm không tồn tại!");
                }

                // Kiểm tra sản phẩm đã được áp dụng phiếu giảm giá
                for (SanPham sanPham : sanPhamList) {
                    for (SanPhamChiTiet sanPhamChiTiet : spctRepo.findBySanPhamId(sanPham.getId())) {
                        if (pggspRepo.findBySanPhamChiTietId(sanPhamChiTiet.getId()).isPresent()) {
                            throw new IllegalArgumentException("Sản phẩm '" + sanPham.getTen() + "' đã được áp dụng phiếu giảm giá trước đó!");
                        }
                    }
                }

                // Tạo và lưu liên kết phiếu giảm giá - sản phẩm
                for (SanPham sanPham : sanPhamList) {
                    for (SanPhamChiTiet sanPhamChiTiet : spctRepo.findBySanPhamId(sanPham.getId())) {
                        PhieuGiamSanPham pggSanPham = new PhieuGiamSanPham();
                        pggSanPham.setPhieuGiam(pgg);
                        pggSanPham.setSanPham(sanPham);
                        pggSanPham.setSanPhamChiTiet(sanPhamChiTiet);

                        BigDecimal giaSauGiam = sanPhamChiTiet.getGia().subtract(pgg.getGiaTriGiam());
                        pggSanPham.setGiaSauGiam(giaSauGiam.max(BigDecimal.ZERO)); // Đảm bảo giá không âm
                        pggspRepo.save(pggSanPham);
                    }
                }
            }

            // Lưu phiếu giảm giá
            pggRepo.save(pgg);

            return "redirect:/admin/phieu-giam/index";

        } catch (IllegalArgumentException e) {
            // Gửi lại thông tin cần thiết về giao diện nếu xảy ra lỗi
            model.addAttribute("sanPhams", spRepo.findAll());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pgg", pgg);
            model.addAttribute("sanPhamMa", sanPhamMa);
            return "/admin/phieu_giam/create"; // Trả về giao diện nhập liệu
        }
    }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        // Lấy phiếu giảm giá
        PhieuGiam phieuGiam = pggRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu giảm giá không tồn tại với ID: " + id));

        // Lấy sản phẩm và sản phẩm chi tiết liên kết
        List<PhieuGiamSanPham> pggspList = pggspRepo.findByPhieuGiamId(id);

        SanPham sanPham = null;
        String sanPhamMa = null;

        if (!pggspList.isEmpty()) {
            sanPham = pggspList.get(0).getSanPham();
            sanPhamMa = sanPham.getMa();
        }

        // Gửi dữ liệu sang View
        model.addAttribute("pgg", phieuGiam);
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("sanPhamMa", sanPhamMa);
        model.addAttribute("isEditable", phieuGiam.isTrangThai());

        return "admin/phieu_giam/update";
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
                // Tìm sản phẩm liên kết
                List<SanPhamChiTiet> sanPhamChiTietList = spctRepo.findBySanPhamId(phieuGiam.getSanPham().getId());

                for (SanPhamChiTiet spct : sanPhamChiTietList) {
                    PhieuGiamSanPham newLink = new PhieuGiamSanPham();
                    newLink.setPhieuGiam(phieuGiam);
                    newLink.setSanPham(phieuGiam.getSanPham());
                    newLink.setSanPhamChiTiet(spct);
                    newLink.setGiaSauGiam(spct.getGia().subtract(phieuGiam.getGiaTriGiam()).max(BigDecimal.ZERO));

                    pggspRepo.save(newLink);
                }
            }
        }

        // Lưu phiếu giảm giá đã cập nhật
        pggRepo.save(phieuGiam);

        return "redirect:/admin/phieu-giam/index";
    }
}
