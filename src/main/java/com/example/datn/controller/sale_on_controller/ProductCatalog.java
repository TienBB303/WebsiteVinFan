package com.example.datn.controller.sale_on_controller;

import com.example.datn.entity.*;
import com.example.datn.entity.phieu_giam.PhieuGiam;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.repository.LichSuHoaDonRepo;
import com.example.datn.repository.ThuocTinhRepo.KieuQuatRepo;
import com.example.datn.repository.phieu_giam_repo.PhieuGiamRepo;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class ProductCatalog {

    private final HoaDonService hoaDonService;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
//    @Autowired
//    private PhieuGiamSanPhamRepo phieuGiamSanPhamRepo;
    @Autowired
    private PhieuGiamRepo phieuGiamRepo;
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
            @RequestParam(value = "filter", required = false, defaultValue = "all") String filter) {

        List<SanPhamChiTiet> sanPhamKhongGiamGia;
        List<PhieuGiam> sanPhamGiamGia;

        if (query != null && !query.trim().isEmpty()) {
            // Lọc phiếu giảm giá có liên kết với sản phẩm
            sanPhamGiamGia = phieuGiamRepo.timKiemSanPhamCoGiamGia(query);
            List<Long> sanPhamCoGiamGiaIds = sanPhamGiamGia.stream()
                    .map(pg -> pg.getSpct().getId())
                    .collect(Collectors.toList());
            sanPhamKhongGiamGia = spctRepo.timKiemTheoTen(query)
                    .stream()
                    .filter(sp -> !sanPhamCoGiamGiaIds.contains(sp.getId()))
                    .collect(Collectors.toList());
        } else if (maSanPham != null && !maSanPham.isEmpty()) {
            sanPhamKhongGiamGia = spctRepo.findBySanPhamMa(maSanPham);
            sanPhamGiamGia = phieuGiamRepo.findBySanPhamMa(maSanPham);
        } else {
            // Chỉ lấy phiếu giảm giá có liên kết với sản phẩm
            sanPhamGiamGia = phieuGiamRepo.findAllWithLinkedSpct();
            List<Long> sanPhamCoGiamGiaIds = sanPhamGiamGia.stream()
                    .map(pg -> pg.getSpct().getId())
                    .collect(Collectors.toList());
            sanPhamKhongGiamGia = spctRepo.findByIdNotIn(sanPhamCoGiamGiaIds);
        }

        if ("discounted".equalsIgnoreCase(filter)) {
            sanPhamKhongGiamGia.clear();
        } else if ("non-discounted".equalsIgnoreCase(filter)) {
            sanPhamGiamGia.clear();
        }

        // Lọc theo kiểu quạt (kieuQuatId)
        if (kieuQuatId != null) {
            sanPhamKhongGiamGia = sanPhamKhongGiamGia.stream()
                    .filter(sp -> sp.getSanPham().getKieuQuat().getId().equals(kieuQuatId))
                    .collect(Collectors.toList());

            sanPhamGiamGia = sanPhamGiamGia.stream()
                    .filter(pg -> pg.getSpct().getSanPham().getKieuQuat().getId().equals(kieuQuatId))
                    .collect(Collectors.toList());
        }

        // Lọc theo khoảng giá (minPrice, maxPrice)
        if (minPrice != null || maxPrice != null) {
            BigDecimal finalMinPrice = minPrice != null ? minPrice : BigDecimal.ZERO;
            BigDecimal finalMaxPrice = maxPrice != null ? maxPrice : BigDecimal.valueOf(Double.MAX_VALUE);

            sanPhamKhongGiamGia = sanPhamKhongGiamGia.stream()
                    .filter(sp -> sp.getGia().compareTo(finalMinPrice) >= 0 && sp.getGia().compareTo(finalMaxPrice) <= 0)
                    .collect(Collectors.toList());

            sanPhamGiamGia = sanPhamGiamGia.stream()
                    .filter(pg -> pg.getGiaSauGiam().compareTo(finalMinPrice) >= 0
                            && pg.getGiaSauGiam().compareTo(finalMaxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        model.addAttribute("sanPhamKhongGiamGia", sanPhamKhongGiamGia);
        model.addAttribute("sanPhamGiamGia", sanPhamGiamGia);
        model.addAttribute("query", query);
        model.addAttribute("kieuQuatId", kieuQuatId);
        model.addAttribute("maSanPham", maSanPham);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("filter", filter);
        model.addAttribute("kieuQuats", kieuQuatRepo.findAll());

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
                Optional<PhieuGiam> phieuGiam = phieuGiamRepo.findBySanPhamChiTietId(bienThe.getId());
                phieuGiam.ifPresent(giam -> giaSauGiamMap.put(bienThe.getId(), giam.getGiaSauGiam()));
            });

            // Lấy biến thể đầu tiên làm mặc định
            SanPhamChiTiet defaultBienThe = bienTheList.isEmpty() ? null : bienTheList.get(0);
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
    @GetMapping("/hoa-don-kh/{id}")
    public String detailOrder(@PathVariable long id, Model model) {
        //Lấy thông tin lịch sử hóa đơn theo id hóa đơn
        List<LichSuHoaDon> lichSuHoaDonList = lichSuHoaDonRepo.findLichSuHoaDonByIdHoaDon(id);
        model.addAttribute("listHistory", lichSuHoaDonList);

        List<HoaDonChiTiet> listHDCT = this.hoaDonService.timSanPhamChiTietTheoHoaDon(id);
        model.addAttribute("listHDCT", listHDCT);

        KhachHang khachHang = khachHangRepo.profileKhachHang();
        model.addAttribute("khachHang", khachHang);

        return "admin/website/detailDH";
    }
}
