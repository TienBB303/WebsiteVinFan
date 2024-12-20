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

            // Lưu phiếu giảm giá trước
            pgg = pggRepo.save(pgg);

            if (pgg.isLoaiPhieuGiam()) { // Nếu là giảm giá hóa đơn
                sanPhamMa = null; // Không cần mã sản phẩm
            } else {
                if (sanPhamMa == null || sanPhamMa.trim().isEmpty()) {
                    throw new IllegalArgumentException("Vui lòng chọn mã sản phẩm!");
                }

                // Tìm sản phẩm dựa trên mã
                SanPham sanPham = spRepo.findByMa(sanPhamMa);
                if (sanPham == null) {
                    throw new IllegalArgumentException("Mã sản phẩm không tồn tại!");
                }

                List<SanPhamChiTiet> sanPhamChiTietList = spctRepo.findBySanPhamId(sanPham.getId());
                if (sanPhamChiTietList.isEmpty()) {
                    throw new IllegalArgumentException("Sản phẩm '" + sanPham.getTen() + "' chưa có giá chi tiết!");
                }

                for (SanPhamChiTiet sanPhamChiTiet : sanPhamChiTietList) {
                    if (pggspRepo.findBySanPhamChiTietId(sanPhamChiTiet.getId()).isPresent()) {
                        throw new IllegalArgumentException(
                                "Sản phẩm '" + sanPham.getTen() + "' đã được áp dụng phiếu giảm giá trước đó!"
                        );
                    }

                    // Lấy giá sản phẩm
                    BigDecimal giaSanPham = sanPhamChiTiet.getGia();
                    BigDecimal maxGiam;

                    // Xác định mức giảm tối đa dựa trên giá sản phẩm
                    if (giaSanPham.compareTo(BigDecimal.valueOf(100000)) >= 0 &&
                            giaSanPham.compareTo(BigDecimal.valueOf(200000)) < 0) {
                        maxGiam = BigDecimal.valueOf(50000);
                    } else if (giaSanPham.compareTo(BigDecimal.valueOf(200000)) >= 0 &&
                            giaSanPham.compareTo(BigDecimal.valueOf(500000)) < 0) {
                        maxGiam = BigDecimal.valueOf(100000);
                    } else if (giaSanPham.compareTo(BigDecimal.valueOf(500000)) >= 0 &&
                            giaSanPham.compareTo(BigDecimal.valueOf(1000000)) < 0) {
                        maxGiam = BigDecimal.valueOf(250000);
                    } else if (giaSanPham.compareTo(BigDecimal.valueOf(1000000)) >= 0 &&
                            giaSanPham.compareTo(BigDecimal.valueOf(10000000)) < 0) {
                        maxGiam = BigDecimal.valueOf(500000);
                    } else if (giaSanPham.compareTo(BigDecimal.valueOf(10000000)) >= 0) {
                        maxGiam = BigDecimal.valueOf(1000000);
                    } else {
                        maxGiam = BigDecimal.ZERO; // Trường hợp giá sản phẩm không hợp lệ
                    }

                    if (pgg.getGiaTriGiam().compareTo(maxGiam) > 0) {
                        throw new IllegalArgumentException(
                                "Giá trị giảm không được vượt quá " + maxGiam + " VND cho mức giá sản phẩm " + giaSanPham + " VND."
                        );
                    }

                    // Tạo và lưu liên kết phiếu giảm giá - sản phẩm
                    PhieuGiamSanPham pggSanPham = new PhieuGiamSanPham();
                    pggSanPham.setPhieuGiam(pgg);
                    pggSanPham.setSanPham(sanPham);
                    pggSanPham.setSanPhamChiTiet(sanPhamChiTiet);

                    BigDecimal giaSauGiam = giaSanPham.subtract(pgg.getGiaTriGiam());
                    pggSanPham.setGiaSauGiam(giaSauGiam.max(BigDecimal.ZERO));
                    pggspRepo.save(pggSanPham); // Lưu liên kết sau khi `pgg` đã được lưu
                }
            }

            return "redirect:/admin/phieu-giam/index";

        } catch (IllegalArgumentException e) {
            // Xử lý lỗi khi xảy ra
            List<SanPham> sanPhams = spRepo.findAll();

            // Xử lý mô tả sản phẩm (thêm giá)
            sanPhams.forEach(sp -> {
                List<SanPhamChiTiet> chiTietList = spctRepo.findBySanPhamId(sp.getId());
                if (!chiTietList.isEmpty()) {
                    BigDecimal gia = chiTietList.get(0).getGia();
                    sp.setMo_ta("Giá: " + (gia != null ? gia.toString() : "Chưa có giá"));
                } else {
                    sp.setMo_ta("Chưa có giá");
                }
            });

            model.addAttribute("sanPhams", sanPhams);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pgg", pgg);
            model.addAttribute("sanPhamMa", sanPhamMa);

            return "/admin/phieu_giam/create";
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
    public String update(@ModelAttribute PhieuGiam phieuGiam, Model model) {
        try {
            LocalDate currentDate = LocalDate.now();
            phieuGiam.setNgaySua(Date.valueOf(currentDate));

            // Lưu phiếu giảm giá được cập nhật
            pggRepo.save(phieuGiam);

            // Kiểm tra trạng thái của phiếu giảm giá
            if (phieuGiam.isTrangThai()) {
                // Nếu trạng thái là "Áp dụng lại" hoặc "Áp dụng", cập nhật liên kết sản phẩm
                List<PhieuGiamSanPham> links = pggspRepo.findByPhieuGiamId(phieuGiam.getId());
                for (PhieuGiamSanPham link : links) {
                    // Lấy thông tin sản phẩm chi tiết
                    SanPhamChiTiet spct = link.getSanPhamChiTiet();

                    // Cập nhật số tiền giảm mới
                    BigDecimal giaSauGiam = spct.getGia().subtract(phieuGiam.getGiaTriGiam()).max(BigDecimal.ZERO);
                    link.setGiaSauGiam(giaSauGiam);

                    // Lưu lại liên kết
                    pggspRepo.save(link);
                }
            } else {
                // Nếu trạng thái là "Ngừng áp dụng", xóa liên kết sản phẩm
                List<PhieuGiamSanPham> links = pggspRepo.findByPhieuGiamId(phieuGiam.getId());
                pggspRepo.deleteAll(links); // Xóa toàn bộ liên kết
            }

            return "redirect:/admin/phieu-giam/index";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật phiếu giảm giá: " + e.getMessage());
            model.addAttribute("pgg", phieuGiam);
            return "admin/phieu_giam/update";
        }
    }
}
