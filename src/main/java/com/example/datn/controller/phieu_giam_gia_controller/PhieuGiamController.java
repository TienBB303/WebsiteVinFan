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
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/phieu-giam")
@RequiredArgsConstructor
public class PhieuGiamController {

    private final PhieuGiamRepo pggRepo;
    private final PhieuGiamService pggSV;
    private final SanPhamRepo spRepo;
    private final PhieuGiamSanPhamRepo pggspRepo;
    private final SPCTRepo spctRepo;

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

        // Lấy tất cả phiếu giảm giá
        List<PhieuGiam> allDiscounts = pggRepo.findAll();

        // Cập nhật trạng thái phiếu giảm giá dựa vào ngày kết thúc
        allDiscounts.forEach(pgg -> {
            if (pgg.getNgayKT() != null &&
                    pgg.getNgayKT().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now()) &&
                    pgg.isTrangThai()) {
                // Ngừng áp dụng phiếu giảm giá
                pgg.setTrangThai(false);
                pggRepo.save(pgg);

                // Xóa các liên kết trong bảng phieu_giam_san_pham
                List<PhieuGiamSanPham> links = pggspRepo.findByPhieuGiamId(pgg.getId());
                if (!links.isEmpty()) {
                    links.forEach(link -> pggspRepo.delete(link));
                }
            }
        });

        // Lọc kết quả sau khi cập nhật
        Page<PhieuGiam> phieuGiamGiaPage;
        if (status != null) {
            phieuGiamGiaPage = pggRepo.findByTenLikeAndTrangThai(searchTerm, status, pageable);
        } else {
            phieuGiamGiaPage = pggRepo.findByTenLike(searchTerm, pageable);
        }

        // Truyền dữ liệu sang View
        model.addAttribute("ListPGG", phieuGiamGiaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", phieuGiamGiaPage.getTotalPages());

        return "admin/phieu_giam/index";
    }

    @GetMapping("/store")
    public String store(Model model) {
        List<SanPhamChiTiet> sanPhamChiTietList = spctRepo.findAll();

        // Lấy danh sách sản phẩm đã áp dụng phiếu giảm giá
        Map<Long, Boolean> appliedProductMap = pggspRepo.findAll().stream()
                .collect(Collectors.toMap(
                        pggsp -> pggsp.getSanPhamChiTiet().getId(),
                        pggsp -> true,
                        (existing, replacement) -> existing
                ));

        model.addAttribute("sanPhamChiTietList", sanPhamChiTietList);
        model.addAttribute("appliedProductMap", appliedProductMap);

        return "/admin/phieu_giam/create";
    }
    @GetMapping("/discount")
    public String showDiscountProducts(Model model) {
        List<PhieuGiamSanPham> discountProducts = pggspRepo.findAll(); // Truy vấn tất cả thông tin
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
            phieuGiam.setMa(pggSV.taoMaTuDong());

            // Kiểm tra và gán SanPhamChiTiet nếu spctId không null
            if (spctId != null) {
                // Lấy thông tin sản phẩm chi tiết
                SanPhamChiTiet spct = spctRepo.findById(spctId)
                        .orElseThrow(() -> new IllegalArgumentException("Sản phẩm chi tiết không tồn tại."));

                // Kiểm tra nếu sản phẩm chi tiết đã có phiếu giảm giá
                if (pggspRepo.findBySanPhamChiTietId(spctId).isPresent()) {
                    throw new IllegalArgumentException("Sản phẩm đã áp dụng phiếu giảm giá.");
                }

                // Gán sản phẩm chi tiết và sản phẩm vào phiếu giảm giá
                phieuGiam.setSpct(spct);
                phieuGiam.setSanPham(spct.getSanPham());
            }

            // Lưu Phiếu Giảm Giá
            phieuGiam = pggRepo.save(phieuGiam);

            // Nếu có sản phẩm chi tiết, lưu vào PhieuGiamSanPham
            if (spctId != null) {
                SanPhamChiTiet spct = phieuGiam.getSpct();
                BigDecimal giaSauGiam = spct.getGia().subtract(phieuGiam.getGiaTriGiam()).max(BigDecimal.ZERO);

                // Tạo và lưu đối tượng PhieuGiamSanPham
                PhieuGiamSanPham pggsp = new PhieuGiamSanPham();
                pggsp.setPhieuGiam(phieuGiam);
                pggsp.setSanPhamChiTiet(spct);
                pggsp.setSanPham(spct.getSanPham());
                pggsp.setGiaSauGiam(giaSauGiam);

                // Lưu vào cơ sở dữ liệu
                pggspRepo.save(pggsp);
            }

            return "redirect:/admin/phieu-giam/index";
        } catch (Exception e) {
            // Lấy danh sách sản phẩm chi tiết để hiển thị lại khi có lỗi
            List<SanPhamChiTiet> sanPhamChiTietList = spctRepo.findAll();

            // Lấy danh sách sản phẩm đã áp dụng phiếu giảm giá
            Map<Long, Boolean> appliedProductMap = pggspRepo.findAll().stream()
                    .collect(Collectors.toMap(
                            pggsp -> pggsp.getSanPhamChiTiet().getId(),
                            pggsp -> true,
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

        // Lấy danh sách sản phẩm chi tiết liên quan đến phiếu giảm giá
        List<PhieuGiamSanPham> pggspList = pggspRepo.findByPhieuGiamId(id);

        String sanPhamThongTin = "Không có sản phẩm áp dụng";
        BigDecimal giaSanPham = BigDecimal.ZERO;

        if (!pggspList.isEmpty()) {
            PhieuGiamSanPham pggsp = pggspList.get(0); // Lấy sản phẩm đầu tiên trong danh sách
            SanPhamChiTiet spct = pggsp.getSanPhamChiTiet();

            if (spct != null) {
                // Định dạng thông tin sản phẩm chi tiết
                sanPhamThongTin = spct.getSanPham().getTen() + " - " +
                        spct.getMauSac().getTen() + " - Công suất: " +
                        spct.getCongSuat().getTen() + "W - Giá: " +
                        (spct.getGia() != null ? spct.getGia().toString() + " VND" : "Chưa có giá");
                giaSanPham = spct.getGia() != null ? spct.getGia() : BigDecimal.ZERO;
            }
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

            // Lấy danh sách sản phẩm chi tiết liên quan
            List<PhieuGiamSanPham> links = pggspRepo.findByPhieuGiamId(phieuGiam.getId());

            // Kiểm tra nếu không có sản phẩm chi tiết liên quan
            if (links.isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy sản phẩm chi tiết liên quan đến phiếu giảm giá.");
            }

            for (PhieuGiamSanPham link : links) {
                // Lấy thông tin sản phẩm chi tiết từ cơ sở dữ liệu
                SanPhamChiTiet spct = spctRepo.findById(link.getSanPhamChiTiet().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Sản phẩm chi tiết không tồn tại."));

                // Kiểm tra nếu giá trị giảm vượt quá mức tối đa
                BigDecimal giaSanPham = spct.getGia();
                if (giaSanPham == null) {
                    throw new IllegalArgumentException("Sản phẩm không có thông tin giá. Vui lòng kiểm tra lại.");
                }

                BigDecimal maxGiam = calculateMaxDiscount(giaSanPham);
                if (phieuGiam.getGiaTriGiam().compareTo(maxGiam) > 0) {
                    throw new IllegalArgumentException(
                            "Giá trị giảm không được vượt quá " + maxGiam + " VND cho mức giá sản phẩm " + giaSanPham + " VND.");
                }

                // Cập nhật số tiền giảm mới
                BigDecimal giaSauGiam = giaSanPham.subtract(phieuGiam.getGiaTriGiam()).max(BigDecimal.ZERO);
                link.setGiaSauGiam(giaSauGiam);

                // Đảm bảo không ghi đè id và idspct thành null
                link.setSanPhamChiTiet(spct);
                link.setPhieuGiam(existingPhieuGiam);

                // Lưu lại liên kết
                pggspRepo.save(link);
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
