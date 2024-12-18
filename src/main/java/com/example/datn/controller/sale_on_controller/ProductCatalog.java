package com.example.datn.controller.sale_on_controller;

import com.example.datn.entity.phieu_giam.PhieuGiamSanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.ThuocTinhRepo.KieuQuatRepo;
import com.example.datn.repository.phieu_giam_repo.PhieuGiamSanPhamRepo;
import com.example.datn.repository.SPCTRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class ProductCatalog {


    @Autowired
    private PhieuGiamSanPhamRepo phieuGiamSanPhamRepo;
    @Autowired
    private SPCTRepo spctRepo;
    @Autowired
    private KieuQuatRepo kieuQuatRepo;

    @GetMapping("/product-catalog")
    public String productCatalog(
            Model model,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "kieuQuatId", required = false) Integer kieuQuatId,
            @RequestParam(value = "maSanPham", required = false) String maSanPham) {

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

        // Lọc theo kiểu quạt nếu có
        if (kieuQuatId != null) {
            sanPhamKhongGiamGia = sanPhamKhongGiamGia.stream()
                    .filter(sp -> sp.getSanPham().getKieuQuat().getId().equals(kieuQuatId))
                    .collect(Collectors.toList());

            sanPhamGiamGia = sanPhamGiamGia.stream()
                    .filter(pg -> pg.getSanPhamChiTiet().getSanPham().getKieuQuat().getId().equals(kieuQuatId))
                    .collect(Collectors.toList());
        }

        // Xóa trùng lặp sản phẩm theo mã
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
        model.addAttribute("kieuQuats", kieuQuatRepo.findAll());

        String currentPrincipalName = authentication.getName();
        model.addAttribute("currentPrincipalName", currentPrincipalName);

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

}
