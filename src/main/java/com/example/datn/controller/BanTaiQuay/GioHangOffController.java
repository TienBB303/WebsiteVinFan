package com.example.datn.controller.BanTaiQuay;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.service.GioHangOffService;
import com.example.datn.service.HoaDonService;
import com.example.datn.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ban-hang-tai-quay")
public class GioHangOffController {
    @Autowired
    private GioHangOffService gioHangOffService;

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping("/ban-hang")
    public String listSanPhamVaGioHang(@RequestParam(value = "hoaDonId", required = false) Long hoaDonId, Model model) {
        List<SanPhamChiTiet> sanPhams = sanPhamService.findAll();
        model.addAttribute("sanPhams", sanPhams);

        gioHangOffService.taoHoaDonMoi();
        List<HoaDon> hoaDons = hoaDonService.getAllHoaDon();
        model.addAttribute("hoaDons", hoaDons);

        if (hoaDonId != null) {
            Map<Long, Integer> cartSanPhams = gioHangOffService.getCart(hoaDonId);
            model.addAttribute("cartSanPhams", cartSanPhams);
        }

        return "admin/ban_hang_tai_quay/index";
    }


    @PostMapping("/gio-hang-them")
    public String themVaoGio(@RequestParam("hoaDonId") Long hoaDonId,
                             @RequestParam("id") Long sanPhamChiTietId,
                             @RequestParam("soLuong") int soLuong) {
        gioHangOffService.themVaoGio(hoaDonId, sanPhamChiTietId, soLuong);
        return "redirect:/ban-hang-tai-quay/ban-hang?hoaDonId=" + hoaDonId;
    }

    @PostMapping("/gio-hang/thanh-toan")
    public String checkout(@RequestParam("hoaDonId") Long hoaDonId, Model model) {
        if (gioHangOffService.thanhToan(hoaDonId)) {
            return "redirect:/ban-hang-tai-quay/hoa-don-thanh-cong";
        } else {
            model.addAttribute("error", "Không thể thanh toán, kiểm tra lại số lượng sản phẩm.");
            return "redirect:/ban-hang-tai-quay/ban-hang";
        }
    }
    @PostMapping("/gio-hang/them-moi")
    public String taoHoaDonMoi() {
        Long hoaDonId = gioHangOffService.taoHoaDonMoi();
        return "redirect:/ban-hang-tai-quay/ban-hang?hoaDonId=" + hoaDonId;
    }

}
