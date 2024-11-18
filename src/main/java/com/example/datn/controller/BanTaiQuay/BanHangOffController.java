package com.example.datn.controller.BanTaiQuay;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonOff;
import com.example.datn.entity.HoaDonOffChiTiet;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.BanOffRepo.HoaDonOffChiTietRepo;
import com.example.datn.repository.BanOffRepo.HoaDonOffRepo;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.service.BanHangOffService;
import com.example.datn.service.HoaDonService;
import com.example.datn.service.SanPhamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ban-hang-tai-quay")
public class BanHangOffController {
    @Autowired
    private BanHangOffService banHangOffService;

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping("/ban-hang")
    public String listSanPhamVaGioHang(
            @RequestParam(value = "hoaDonId", required = false) Long hoaDonOffId,
            @RequestParam(value = "search", required = false) String search,
            Model model) {

        // Danh sách sản phẩm
        List<SanPhamChiTiet> sanPhams = (search == null || search.isBlank())
                ? sanPhamService.findAll()
                : sanPhamService.timSanPhamTheoTen(search);
        model.addAttribute("sanPhams", sanPhams);

        // Danh sách hóa đơn
        List<HoaDonOff> hoaDons = banHangOffService.findAllHoaDon();
        model.addAttribute("hoaDons", hoaDons);

        // Giỏ hàng của hóa đơn hiện tại
        if (hoaDonOffId != null) {
            List<HoaDonOffChiTiet> sanPhamTrongGio = banHangOffService.getChiTietByHoaDonId(hoaDonOffId);
            model.addAttribute("sanPhamTrongGio", sanPhamTrongGio);
            model.addAttribute("tongTien", banHangOffService.getTongTien(hoaDonOffId));
            model.addAttribute("idHoaDonHienTai", hoaDonOffId);
        }

        return "admin/ban_hang_tai_quay/index";
    }


    @PostMapping("/tao-hoa-don")
    public String taoHoaDon() {
        HoaDonOff hoaDonOff = banHangOffService.taoHoaDon();
        return "redirect:/ban-hang-tai-quay/ban-hang?hoaDonId=" + hoaDonOff.getId();
    }

    @PostMapping("/gio-hang-them")
    public String themSPvaoGio(
            @RequestParam("idSanPham") Long id,
            @RequestParam("soLuong") int soLuong,
            @RequestParam("hoaDonOffId") Long hoaDonOffId) {
        banHangOffService.themSanPhamVaoGio(hoaDonOffId, id, soLuong);
        return "redirect:/ban-hang-tai-quay/ban-hang?hoaDonId=" + hoaDonOffId;
    }

    @PostMapping("/gio-hang-thanh-toan")
    public String thanhToanHoaDon(@RequestParam Long hoaDonOffId) {
        banHangOffService.thanhToanHoaDon(hoaDonOffId);
        return "redirect:/ban-hang-tai-quay/ban-hang";
    }
}


