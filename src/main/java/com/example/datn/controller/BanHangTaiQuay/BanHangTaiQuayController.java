package com.example.datn.controller.BanHangTaiQuay;

import com.example.datn.dto.request.AddSPToHoaDonChiTietRequest;
import com.example.datn.dto.response.ListSanPhamInHoaDonChiTietResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.service.BanHangTaiQuay.BanHangTaiQuayService;
import com.example.datn.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ban-hang-tai-quay")
public class BanHangTaiQuayController {
    private final BanHangTaiQuayService banHangTaiQuayService;
    private final HoaDonService hoaDonService;
    private final SPCTRepo spctRepo;

    @GetMapping("/index")
    public String index(
            Model model
    ) {
        //hiển thị danh sách hóa đơn chờ
        List<HoaDon> listHoaDon = banHangTaiQuayService.findHoaDon();
        model.addAttribute("listHoaDon", listHoaDon);

        //hiển thị danh sách sản phẩm add
        List<SanPhamChiTiet> listSPCTInHDCT = this.hoaDonService.getSPCTInHDCT();
        model.addAttribute("listSPCTInHDCT", listSPCTInHDCT);

        return "admin/ban_hang_tai_quay/index";
    }

    @GetMapping("/hdct")
    public String viewHDCT(
            Model model,
            @RequestParam("idHD") Long idHD
    ) {

        //hiển thị danh sách hóa đơn chờ
        List<HoaDon> listHoaDon = banHangTaiQuayService.findHoaDon();
        model.addAttribute("listHoaDon", listHoaDon);

        //hiển thị danh sách sản phẩm add
        List<SanPhamChiTiet> listSPCTInHDCT = this.hoaDonService.getSPCTInHDCT();
        model.addAttribute("listSPCTInHDCT", listSPCTInHDCT);

        // hiển thị thông tin spct
//        List<ListSanPhamInHoaDonChiTietResponse> listHDCT = this.hoaDonService.getSanPhamCTByHoaDonId(idHD);
//        model.addAttribute("listHDCT", listHDCT);

        List<HoaDonChiTiet> listHDCT1 = this.hoaDonService.timSanPhamChiTietTheoHoaDon(idHD);
        model.addAttribute("listHDCT", listHDCT1);


        // Lấy tổng tiền từ service
        BigDecimal tongTien = banHangTaiQuayService.getTongTien(idHD);
        model.addAttribute("tongTien", tongTien);

        KhachHang khachHang = banHangTaiQuayService.getKhachHangLe(1L); // Lấy khách hàng có id = 1
        model.addAttribute("khachHang", khachHang);

        // Thêm ID hóa đơn được chọn vào model
        model.addAttribute("idHD", idHD);
        return "admin/ban_hang_tai_quay/index";
    }

    @PostMapping("/addSPCT")
    public String addSPToHoaDonChiTiet(
            @ModelAttribute AddSPToHoaDonChiTietRequest request
    ) {
        try {
            hoaDonService.addSpToHoaDonChiTietRequestList(request); // Gọi service để thêm sản phẩm vào hóa đơn
            hoaDonService.updateTongTienHoaDon();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/ban-hang-tai-quay/hdct?idHD=" + request.getIdHD();
    }

    @PostMapping("/tao-hoa-don")
    public String taoHoaDon() {
        HoaDon hoaDon = new HoaDon();
        banHangTaiQuayService.taoHoaDonCho(hoaDon);
        return "redirect:/ban-hang-tai-quay/index";
    }

    @PostMapping("/xoa")
    public String deleteChiTiet(
            @RequestParam("idHD") Long idHD,
            @RequestParam("idSP") Long idSP) {
        try {
            hoaDonService.deleteSPInHD(idHD, idSP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/ban-hang-tai-quay/hdct?idHD=" + idHD;
    }

}
