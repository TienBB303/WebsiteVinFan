package com.example.datn.controller;

import com.example.datn.entity.PhieuGiamSanPham;
import com.example.datn.entity.san_pham.SanPhamChiTiet;
import com.example.datn.repository.PhieuGiamSanPhamRepo;
import com.example.datn.repository.san_pham_repo.SPCTRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class ProductCatalog {


    @Autowired
    private PhieuGiamSanPhamRepo phieuGiamSanPhamRepo;
    @Autowired
    private SPCTRepo spctRepo;

    @GetMapping("/product-catalog")
    public String productCatalog(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Lấy danh sách sản phẩm chi tiết chưa áp dụng giảm giá
        List<SanPhamChiTiet> sanPhamKhongGiamGia = spctRepo.findByIdNotIn(
                phieuGiamSanPhamRepo.findAllSanPhamChiTietIds());

        if (sanPhamKhongGiamGia == null) {
            sanPhamKhongGiamGia = new ArrayList<>();  // Gán giá trị rỗng nếu không tìm thấy sản phẩm
        }

        // Lấy danh sách sản phẩm chi tiết có áp dụng giảm giá
        List<PhieuGiamSanPham> sanPhamGiamGia = phieuGiamSanPhamRepo.findAll();

        if (sanPhamGiamGia == null) {
            sanPhamGiamGia = new ArrayList<>();  // Gán giá trị rỗng nếu không tìm thấy sản phẩm giảm giá
        }

        // Tính toán giá sau khi giảm cho các sản phẩm có giảm giá
        for (PhieuGiamSanPham phieuGiamSanPham : sanPhamGiamGia) {
            if (phieuGiamSanPham.getSanPhamChiTiet() != null) {  // Kiểm tra tránh null
                BigDecimal giaGoc = phieuGiamSanPham.getSanPhamChiTiet().getGia();
                BigDecimal giaTriGiam = phieuGiamSanPham.getPhieuGiam().getGiaTriGiam();
                BigDecimal giaSauGiam = giaGoc.subtract(giaTriGiam);
                phieuGiamSanPham.setGiaSauGiam(giaSauGiam);  // Cập nhật giá sau khi giảm
            }
        }

        // Đưa danh sách sản phẩm chi tiết vào model
        model.addAttribute("sanPhamKhongGiamGia", sanPhamKhongGiamGia);
        model.addAttribute("sanPhamGiamGia", sanPhamGiamGia);
        String currentPrincipalName = authentication.getName();

        model.addAttribute("currentPrincipalName", currentPrincipalName);

        return "/admin/website/productCatalog";
    }
}
