package com.example.datn.controller.phieu_giam_gia_controller;

import com.example.datn.entity.phieu_giam.PhieuGiam;
import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.phieu_giam_repo.PhieuGiamRepo;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.service.phieu_giam_service.PhieuGiamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/phieu-giam")
@RequiredArgsConstructor
public class PhieuGiamController {

    private final PhieuGiamRepo pggRepo;
    private final PhieuGiamService pggSV;
    private final SPCTRepo spctRepo;

    @GetMapping("/index")
    public String phieuGiam(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "status", required = false) Boolean status
    ) {
        // Đảm bảo page >= 0
        if (page < 0) {
            page = 0;
        }

        // Tạo Pageable để phân trang
        PageRequest pageable = PageRequest.of(page, size);
        String searchTerm = "%" + keyword + "%";

        // Lấy tất cả phiếu giảm giá và cập nhật trạng thái
        List<PhieuGiam> allDiscounts = pggRepo.findAll();

        allDiscounts.forEach(pgg -> {
            if (pgg.getNgayKT() != null &&
                    pgg.getNgayKT().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now()) &&
                    pgg.isTrangThai()) {

                // Nếu phiếu giảm giá đã hết hạn, cập nhật trạng thái
                pgg.setTrangThai(false);
                pggRepo.save(pgg);

                // Xóa các sản phẩm liên kết trong phiếu giảm giá (nếu cần)
                if (pgg.getSpct() != null) {
                    pgg.setSpct(null); // Loại bỏ liên kết với sản phẩm
                }

                pggRepo.save(pgg); // Lưu lại sau khi xóa liên kết
            }
        });

        // Lọc phiếu giảm giá theo từ khóa và trạng thái
        Page<PhieuGiam> phieuGiamGiaPage;
        if (status != null) {
            phieuGiamGiaPage = pggRepo.findByTenLikeAndTrangThai(searchTerm, status, pageable);
        } else {
            phieuGiamGiaPage = pggRepo.findByTenLike(searchTerm, pageable);
        }

        // Đảm bảo không có trường hợp null trong danh sách
        List<PhieuGiam> sanitizedPhieuGiamList = phieuGiamGiaPage.getContent().stream().map(pgg -> {
            if (pgg.getSpct() == null) {
                pgg.setSpct(new SanPhamChiTiet());
            }
            if (pgg.getSpct().getSanPham() == null) {
                pgg.getSpct().setSanPham(new SanPham());
            }
            return pgg;
        }).toList();

        // Truyền dữ liệu sang View
        model.addAttribute("ListPGG", sanitizedPhieuGiamList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", phieuGiamGiaPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "admin/phieu_giam/index";
    }

    @GetMapping("/store")
    public String store(Model model) {
        // Lấy danh sách sản phẩm chi tiết
        List<SanPhamChiTiet> sanPhamChiTietList = spctRepo.findAll();

        // Lấy danh sách các sản phẩm đã áp dụng phiếu giảm giá đang hoạt động
        Map<Long, Boolean> appliedProductMap = pggRepo.findAll().stream()
                .filter(pgg -> pgg.getSpct() != null && pgg.isTrangThai()) // Lọc phiếu giảm giá đang hoạt động
                .collect(Collectors.toMap(
                        pgg -> pgg.getSpct().getId(),
                        pgg -> true,
                        (existing, replacement) -> existing
                ));

        model.addAttribute("sanPhamChiTietList", sanPhamChiTietList);
        model.addAttribute("appliedProductMap", appliedProductMap);

        return "/admin/phieu_giam/create";
    }

    @GetMapping("/discount")
    public String showDiscountProducts(Model model) {
        List<PhieuGiam> discountProducts = pggRepo.findAll(); // Truy vấn tất cả thông tin
        model.addAttribute("discountProducts", discountProducts); // Thêm vào model
        return "/admin/phieu_giam/cart"; // Trả về view
    }

    @PostMapping("/add")
    public String add(@ModelAttribute PhieuGiam phieuGiam,
                      @RequestParam(value = "sanPhamChiTietId", required = false) Long spctId,
                      Model model) {
        try {
            // Thiết lập thông tin cơ bản cho phiếu giảm giá
            phieuGiam.setNgayTao(Date.valueOf(LocalDate.now()));
            phieuGiam.setNguoiTao("admin");
            phieuGiam.setTrangThai(true);
            phieuGiam.setMa(pggSV.taoMaTuDong());

            // Xử lý chỉ khi `spctId` không null
            if (spctId != null) {
                // Lấy thông tin sản phẩm chi tiết
                SanPhamChiTiet spct = spctRepo.findById(spctId)
                        .orElseThrow(() -> new IllegalArgumentException("Sản phẩm chi tiết không tồn tại."));

                // Kiểm tra nếu sản phẩm chi tiết đã có phiếu giảm giá
                if (pggRepo.findBySanPhamChiTietId(spctId).isPresent()) {
                    throw new IllegalArgumentException("Sản phẩm đã áp dụng phiếu giảm giá.");
                }

                // Gán sản phẩm chi tiết vào phiếu giảm giá
                phieuGiam.setSpct(spct);

                // Tính giá sau giảm
                BigDecimal giaSauGiam = spct.getGia().subtract(phieuGiam.getGiaTriGiam()).max(BigDecimal.ZERO);
                phieuGiam.setGiaSauGiam(giaSauGiam); // Gán giá sau giảm trực tiếp vào phiếu giảm giá
            }

            // Lưu Phiếu Giảm Giá
            pggRepo.save(phieuGiam);

            return "redirect:/admin/phieu-giam/index";
        } catch (Exception e) {
            // Lấy danh sách sản phẩm chi tiết để hiển thị lại khi có lỗi
            List<SanPhamChiTiet> sanPhamChiTietList = spctRepo.findAll();

            // Lấy danh sách sản phẩm đã áp dụng phiếu giảm giá
            Map<Long, Boolean> appliedProductMap = pggRepo.findAll().stream()
                    .filter(p -> p.getSpct() != null)
                    .collect(Collectors.toMap(
                            p -> p.getSpct().getId(),
                            p -> true,
                            (existing, replacement) -> existing
                    ));

            // Truyền dữ liệu sang view
            model.addAttribute("sanPhamChiTietList", sanPhamChiTietList);
            model.addAttribute("appliedProductMap", appliedProductMap);
            model.addAttribute("errorMessage", e.getMessage());

            return "admin/phieu_giam/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        // Lấy phiếu giảm giá
        PhieuGiam phieuGiam = pggRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu giảm giá không tồn tại với ID: " + id));


        // Lấy sản phẩm chi tiết liên quan đến phiếu giảm giá
        SanPhamChiTiet spct = phieuGiam.getSpct();

        String sanPhamThongTin = "Không có sản phẩm áp dụng";
        BigDecimal giaSanPham = BigDecimal.ZERO;


        if (spct != null) {
            // Định dạng thông tin sản phẩm chi tiết
            sanPhamThongTin = spct.getSanPham().getTen() + " - " +
                    spct.getMauSac().getTen() + " - Công suất: " +
                    spct.getCongSuat().getTen() + "W - Giá: " +
                    (spct.getGia() != null ? spct.getGia().toString() + " VND" : "Chưa có giá");
            giaSanPham = spct.getGia() != null ? spct.getGia() : BigDecimal.ZERO;
        }
        // Gửi dữ liệu sang View
        model.addAttribute("pgg", phieuGiam);
        model.addAttribute("sanPhamThongTin", sanPhamThongTin); // Thông tin sản phẩm
        model.addAttribute("giaSanPham", giaSanPham);           // Giá sản phẩm
        model.addAttribute("isEditable", phieuGiam.isTrangThai());

        return "admin/phieu_giam/update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute PhieuGiam phieuGiam, Model model) {
        try {
            // Lấy phiếu giảm giá hiện tại từ database
            PhieuGiam existingPhieuGiam = pggRepo.findById(phieuGiam.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Phiếu giảm giá không tồn tại."));

            // Cập nhật thông tin cơ bản
            existingPhieuGiam.setNgaySua(Date.valueOf(LocalDate.now()));
            existingPhieuGiam.setTen(phieuGiam.getTen());
            existingPhieuGiam.setNgayBD(phieuGiam.getNgayBD());
            existingPhieuGiam.setNgayKT(phieuGiam.getNgayKT());
            existingPhieuGiam.setGiaTriGiam(phieuGiam.getGiaTriGiam());
            existingPhieuGiam.setTrangThai(phieuGiam.isTrangThai());

            // Kiểm tra trạng thái: nếu chuyển về "ngừng áp dụng", xóa liên kết sản phẩm
            if (!phieuGiam.isTrangThai()) { // Nếu trạng thái là "ngừng áp dụng"
//                existingPhieuGiam.setSpct(null); // Xóa liên kết sản phẩm
                System.out.println("Liên kết với sản phẩm đã được xóa do trạng thái chuyển về ngừng áp dụng.");
            } else {
                // Nếu trạng thái vẫn là "áp dụng", kiểm tra và cập nhật giá sau giảm
                SanPhamChiTiet spct = existingPhieuGiam.getSpct();
                if (spct != null) {
                    BigDecimal giaSanPham = spct.getGia();
                    if (giaSanPham == null) {
                        throw new IllegalArgumentException("Sản phẩm không có thông tin giá. Vui lòng kiểm tra lại.");
                    }

                    BigDecimal maxGiam = calculateMaxDiscount(giaSanPham);
                    if (phieuGiam.getGiaTriGiam().compareTo(maxGiam) > 0) {
                        throw new IllegalArgumentException(
                                "Giá trị giảm không được vượt quá " + maxGiam + " VND cho mức giá sản phẩm " + giaSanPham + " VND.");
                    }

                    BigDecimal giaSauGiam = giaSanPham.subtract(phieuGiam.getGiaTriGiam()).max(BigDecimal.ZERO);
                    existingPhieuGiam.setGiaSauGiam(giaSauGiam);
                }
            }

            // Lưu phiếu giảm giá được cập nhật
            pggRepo.save(existingPhieuGiam);

            return "redirect:/admin/phieu-giam/index";
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi và gửi thông báo lỗi sang View
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pgg", phieuGiam);
            return "admin/phieu_giam/update";
        }
    }

    // Hàm tính mức giảm tối đa
    private BigDecimal calculateMaxDiscount(BigDecimal giaSanPham) {
        if (giaSanPham.compareTo(BigDecimal.valueOf(100000)) >= 0 &&
                giaSanPham.compareTo(BigDecimal.valueOf(200000)) < 0) {
            return BigDecimal.valueOf(50000);
        } else if (giaSanPham.compareTo(BigDecimal.valueOf(200000)) >= 0 &&
                giaSanPham.compareTo(BigDecimal.valueOf(500000)) < 0) {
            return BigDecimal.valueOf(100000);
        } else if (giaSanPham.compareTo(BigDecimal.valueOf(500000)) >= 0 &&
                giaSanPham.compareTo(BigDecimal.valueOf(1000000)) < 0) {
            return BigDecimal.valueOf(250000);
        } else if (giaSanPham.compareTo(BigDecimal.valueOf(1000000)) >= 0 &&
                giaSanPham.compareTo(BigDecimal.valueOf(10000000)) < 0) {
            return BigDecimal.valueOf(500000);
        } else if (giaSanPham.compareTo(BigDecimal.valueOf(10000000)) >= 0) {
            return BigDecimal.valueOf(1000000);
        }
        return BigDecimal.ZERO;
    }

}
