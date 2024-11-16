package com.example.datn.controller.BanTaiQuay;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.service.BanHangOffService;
import com.example.datn.service.HoaDonService;
import com.example.datn.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String listSanPhamVaGioHang(@RequestParam(value = "hoaDonId", required = false) Long hoaDonId, Model model) {
        List<SanPhamChiTiet> sanPhams = sanPhamService.findAll();
        model.addAttribute("sanPhams", sanPhams);

        List<HoaDon> hoaDons = hoaDonService.getAllHoaDon();
        model.addAttribute("hoaDons", hoaDons);

        if (hoaDonId != null) {
            Map<Long, Integer> cartSanPhams = banHangOffService.getCart(hoaDonId);
            model.addAttribute("cartSanPhams", cartSanPhams);
        }

        return "admin/ban_hang_tai_quay/index";
    }


    @PostMapping("/gio-hang-them")
    public String themVaoGio(@RequestParam("hoaDonId") Long hoaDonId,
                             @RequestParam("id") Long sanPhamChiTietId,
                             @RequestParam("soLuong") int soLuong) {
        banHangOffService.themVaoGio(hoaDonId, sanPhamChiTietId, soLuong);
        return "redirect:/ban-hang-tai-quay/ban-hang?hoaDonId=" + hoaDonId;
    }

    @PostMapping("/gio-hang/thanh-toan")
    public String checkout(@RequestParam("hoaDonId") Long hoaDonId, Model model) {
        if (banHangOffService.thanhToan(hoaDonId)) {
            return "redirect:/ban-hang-tai-quay/hoa-don-thanh-cong";
        } else {
            model.addAttribute("error", "Không thể thanh toán, kiểm tra lại số lượng sản phẩm.");
            return "redirect:/ban-hang-tai-quay/ban-hang";
        }
    }

    @PostMapping("/hoa-don/them-moi")
    @ResponseBody
    public ResponseEntity<Long> taoHoaDonMoi() {
        Long hoaDonId = banHangOffService.taoHoaDonMoi();
        return ResponseEntity.ok(hoaDonId); // Trả về ID hóa đơn mới
    }

    @DeleteMapping("/hoa-don/xoa/{idHoaDon}")
    @ResponseBody // Trả về JSON cho JavaScript
    public ResponseEntity<String> xoaHoaDon(@PathVariable Long idHoaDon) {
        HoaDon hoaDon = banHangOffService.findById(idHoaDon);
        if (hoaDon != null) {
            banHangOffService.xoaHoaDon(hoaDon);
            return ResponseEntity.ok("Hóa đơn đã được xóa");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hóa đơn không tồn tại");
    }

}
