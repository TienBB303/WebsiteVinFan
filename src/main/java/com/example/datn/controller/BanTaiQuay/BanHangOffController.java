package com.example.datn.controller.BanTaiQuay;

import com.example.datn.entity.HoaDonOff;
import com.example.datn.entity.HoaDonOffChiTiet;
import com.example.datn.entity.san_pham.SanPhamChiTiet;
import com.example.datn.service.BanHangOffService;
import com.example.datn.service.HoaDonService;
import com.example.datn.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    public void themSanPhamVaoGio(Long hoaDonOffId, Long sanPhamId, int soLuong) {
        HoaDonOff hoaDonOff = banHangOffService.findOnceHoaDon(hoaDonOffId).orElse(null);
        SanPhamChiTiet sanPhamChiTiet = sanPhamService.findById(sanPhamId);

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hay chưa
        HoaDonOffChiTiet hoaDonOffChiTiet = banHangOffService.checkTonTaiSanPhamTrongGio(hoaDonOff, sanPhamChiTiet);

        if (hoaDonOffChiTiet != null) {
            // Nếu sản phẩm đã tồn tại, cập nhật số lượng
            hoaDonOffChiTiet.setSo_luong(hoaDonOffChiTiet.getSo_luong() + soLuong);
            banHangOffService.luuHoaDonOffChiTiet(hoaDonOffChiTiet);
        } else {
            // Nếu sản phẩm chưa có trong giỏ, thêm mới
            hoaDonOffChiTiet = new HoaDonOffChiTiet(hoaDonOff, sanPhamChiTiet, soLuong);
            banHangOffService.luuHoaDonOffChiTiet(hoaDonOffChiTiet);
        }
    }


    @PostMapping("/gio-hang-thanh-toan")
    public String thanhToanHoaDon(@RequestParam Long hoaDonOffId) {
        banHangOffService.thanhToanHoaDon(hoaDonOffId);
        return "redirect:/ban-hang-tai-quay/ban-hang";
    }
}


