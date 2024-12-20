package com.example.datn.controller.sale_on_controller;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.phieu_giam.PhieuGiamSanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.repository.LichSuHoaDonRepo;
import com.example.datn.repository.ThuocTinhRepo.KieuQuatRepo;
import com.example.datn.repository.phieu_giam_repo.PhieuGiamSanPhamRepo;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class ProductCatalog {

    private final HoaDonService hoaDonService;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
    @Autowired
    private PhieuGiamSanPhamRepo phieuGiamSanPhamRepo;
    @Autowired
    private SPCTRepo spctRepo;
    @Autowired
    private KieuQuatRepo kieuQuatRepo;

    @Autowired
    private KhachHangRepo khachHangRepo;

    @GetMapping("/product-catalog")
    public String productCatalog(
            Model model,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "kieuQuatId", required = false) Integer kieuQuatId,
            @RequestParam(value = "maSanPham", required = false) String maSanPham,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "newest") String sortOrder,
            @RequestParam(value = "filter", required = false, defaultValue = "all") String filter) { // Add filter parameter

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<SanPhamChiTiet> sanPhamKhongGiamGia;
        List<PhieuGiamSanPham> sanPhamGiamGia;

        if (query != null && !query.trim().isEmpty()) {
            sanPhamGiamGia = phieuGiamSanPhamRepo.timKiemSanPhamCoGiamGia(query);
            List<Long> sanPhamCoGiamGiaIds = sanPhamGiamGia.stream()
                    .map(p -> p.getSanPhamChiTiet().getId())
                    .collect(Collectors.toList());

            sanPhamKhongGiamGia = spctRepo.timKiemTheoTen(query)
                    .stream()
                    .filter(sp -> !sanPhamCoGiamGiaIds.contains(sp.getId()))
                    .collect(Collectors.toList());

        } else if (maSanPham != null && !maSanPham.isEmpty()) {
            sanPhamKhongGiamGia = spctRepo.findBySanPhamMa(maSanPham);
            sanPhamGiamGia = phieuGiamSanPhamRepo.findBySanPhamMa(maSanPham);
        } else {
            List<Long> sanPhamCoGiamGiaIds = phieuGiamSanPhamRepo.findAllSanPhamChiTietIds();
            sanPhamKhongGiamGia = spctRepo.findByIdNotIn(sanPhamCoGiamGiaIds);
            sanPhamGiamGia = phieuGiamSanPhamRepo.findAll();
        }

        // Apply filter
        if ("discounted".equalsIgnoreCase(filter)) {
            sanPhamKhongGiamGia.clear();
        } else if ("non-discounted".equalsIgnoreCase(filter)) {
            sanPhamGiamGia.clear();
        }

        // Filter by kieuQuatId if provided
        if (kieuQuatId != null) {
            sanPhamKhongGiamGia = sanPhamKhongGiamGia.stream()
                    .filter(sp -> sp.getSanPham().getKieuQuat().getId().equals(kieuQuatId))
                    .collect(Collectors.toList());

            sanPhamGiamGia = sanPhamGiamGia.stream()
                    .filter(pg -> pg.getSanPhamChiTiet().getSanPham().getKieuQuat().getId().equals(kieuQuatId))
                    .collect(Collectors.toList());
        }

        // Filter by price range if provided
        if (minPrice != null || maxPrice != null) {
            BigDecimal finalMinPrice = minPrice != null ? minPrice : BigDecimal.ZERO;
            BigDecimal finalMaxPrice = maxPrice != null ? maxPrice : BigDecimal.valueOf(Double.MAX_VALUE);

            sanPhamKhongGiamGia = sanPhamKhongGiamGia.stream()
                    .filter(sp -> sp.getGia().compareTo(finalMinPrice) >= 0 && sp.getGia().compareTo(finalMaxPrice) <= 0)
                    .collect(Collectors.toList());

            sanPhamGiamGia = sanPhamGiamGia.stream()
                    .filter(pg -> pg.getGiaSauGiam().compareTo(finalMinPrice) >= 0 && pg.getGiaSauGiam().compareTo(finalMaxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        // Sort by creation date
        Comparator<SanPhamChiTiet> sanPhamComparator = Comparator.comparing(SanPhamChiTiet::getNgay_tao);
        Comparator<PhieuGiamSanPham> phieuGiamComparator = Comparator.comparing(pg -> pg.getSanPhamChiTiet().getNgay_tao());

        if ("newest".equalsIgnoreCase(sortOrder)) {
            sanPhamComparator = sanPhamComparator.reversed();
            phieuGiamComparator = phieuGiamComparator.reversed();
        }

        sanPhamKhongGiamGia = sanPhamKhongGiamGia.stream().sorted(sanPhamComparator).collect(Collectors.toList());
        sanPhamGiamGia = sanPhamGiamGia.stream().sorted(phieuGiamComparator).collect(Collectors.toList());

        // Remove duplicates by product code
        Map<String, SanPhamChiTiet> uniqueProducts = new LinkedHashMap<>();
        sanPhamKhongGiamGia.forEach(sp -> uniqueProducts.put(sp.getSanPham().getMa(), sp));

        Map<String, PhieuGiamSanPham> uniqueDiscountedProducts = new LinkedHashMap<>();
        sanPhamGiamGia.forEach(pgg -> uniqueDiscountedProducts.put(
                pgg.getSanPhamChiTiet().getSanPham().getMa(), pgg
        ));

        model.addAttribute("sanPhamKhongGiamGia", uniqueProducts.values());
        model.addAttribute("sanPhamGiamGia", uniqueDiscountedProducts.values());
        model.addAttribute("query", query);
        model.addAttribute("kieuQuatId", kieuQuatId);
        model.addAttribute("maSanPham", maSanPham);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("filter", filter); // Pass filter to the view
        model.addAttribute("kieuQuats", kieuQuatRepo.findAll());

        String currentPrincipalName = authentication.getName();


        return "/admin/website/productCatalog";
    }

    @GetMapping("/detail/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        Optional<SanPhamChiTiet> sanPhamChiTietOpt = spctRepo.findById(id);

        if (sanPhamChiTietOpt.isPresent()) {
            SanPhamChiTiet currentProduct = sanPhamChiTietOpt.get();
            List<SanPhamChiTiet> bienTheList = spctRepo.findAllBySanPhamMa(currentProduct.getSanPham().getMa());

            // Tạo Map để lưu giá sau giảm cho từng biến thể
            Map<Long, BigDecimal> giaSauGiamMap = new HashMap<>();

            bienTheList.forEach(bienThe -> {
                // Tìm giá sau giảm cho từng biến thể
                Optional<PhieuGiamSanPham> phieuGiam = phieuGiamSanPhamRepo.findBySanPhamChiTietId(bienThe.getId());
                phieuGiam.ifPresent(giam -> giaSauGiamMap.put(bienThe.getId(), giam.getGiaSauGiam()));
            });

            // Lấy biến thể cuối cùng làm mặc định
            SanPhamChiTiet defaultBienThe = bienTheList.isEmpty() ? null : bienTheList.get(bienTheList.size() - 1);
            BigDecimal defaultGiaSauGiam = defaultBienThe != null
                    ? giaSauGiamMap.getOrDefault(defaultBienThe.getId(), defaultBienThe.getGia())
                    : BigDecimal.ZERO;

            // Truyền dữ liệu sang Thymeleaf
            model.addAttribute("sanPham", currentProduct.getSanPham());
            model.addAttribute("sanPhamChiTiet", currentProduct); // Truyền SanPhamChiTiet
            model.addAttribute("bienTheList", bienTheList);
            model.addAttribute("giaSauGiamMap", giaSauGiamMap);
            model.addAttribute("defaultBienThe", defaultBienThe); // Biến thể mặc định
            model.addAttribute("defaultGiaSauGiam", defaultGiaSauGiam); // Giá của biến thể mặc định

            return "admin/website/detailSP";
        }

        return "redirect:/cart/view";
    }

    @GetMapping("/track-order")
    public String trackOrder(Model model) {
        KhachHang khachHang = khachHangRepo.profileKhachHang();
        List<HoaDon> hoaDons = hoaDonService.getHoaDonByIdKH(khachHang.getId());

        if (khachHang == null) {
            model.addAttribute("errorMessage", "Không tìm thấy thông tin kh.");
            return "/admin/error"; // Điều hướng đến trang lỗi
        } else {

            // Thêm danh sách hóa đơn vào model
            model.addAttribute("hoaDons", hoaDons);
            model.addAttribute("khachHang", khachHang);
        }
        return "admin/website/trackOrder";
    }
    // Thêm phương thức xử lý AJAX để trả về chi tiết hóa đơn theo ID
    @GetMapping("/hoadon/{id}")
    @ResponseBody
    public HoaDon chiTietHoaDon(@PathVariable("id") Long id) {
        HoaDon hoaDon = hoaDonService.findById(id).orElse(null);
        return hoaDon; // Trả về hóa đơn dưới dạng JSON
    }

}
