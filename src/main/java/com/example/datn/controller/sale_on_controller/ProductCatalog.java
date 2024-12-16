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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            @RequestParam(value = "kieuQuatId", required = false) Integer kieuQuatId) {

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

        model.addAttribute("sanPhamKhongGiamGia", sanPhamKhongGiamGia);
        model.addAttribute("sanPhamGiamGia", sanPhamGiamGia);
        model.addAttribute("query", query);
        model.addAttribute("kieuQuatId", kieuQuatId);
        model.addAttribute("kieuQuats", kieuQuatRepo.findAll()); // Lấy danh sách kiểu quạt để hiển thị lọc

        String currentPrincipalName = authentication.getName();
        model.addAttribute("currentPrincipalName", currentPrincipalName);

        return "/admin/website/productCatalog";
    }
    @GetMapping("/detail/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        // Lấy chi tiết sản phẩm theo id
        Optional<SanPhamChiTiet> sanPhamChiTietOptional = spctRepo.findById(id);

        if (sanPhamChiTietOptional.isPresent()) {
            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietOptional.get();

            // Kiểm tra sản phẩm có thuộc danh sách giảm giá hay không
            Optional<PhieuGiamSanPham> phieuGiamOptional = phieuGiamSanPhamRepo
                    .findBySanPhamChiTietId(id); // Phương thức này cần được định nghĩa trong repository

            model.addAttribute("product", sanPhamChiTiet);  // Đối tượng sanPhamChiTiet được gửi vào model
            if (phieuGiamOptional.isPresent()) {
                PhieuGiamSanPham phieuGiamSanPham = phieuGiamOptional.get();
                model.addAttribute("phieuGiam", phieuGiamSanPham);  // Đối tượng phieuGiam được gửi vào model nếu có
            }
            return "admin/website/detailSP";  // Chuyển đến view chi tiết sản phẩm
        }
        // Nếu không tìm thấy sản phẩm, chuyển hướng về trang danh sách sản phẩm
        return "redirect:/admin/product-catalog";
    }
}
