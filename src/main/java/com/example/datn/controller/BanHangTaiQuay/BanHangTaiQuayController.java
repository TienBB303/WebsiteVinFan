package com.example.datn.controller.BanHangTaiQuay;

import com.example.datn.dto.request.AddSPToHoaDonChiTietRequest;
import com.example.datn.dto.response.ListSanPhamInHoaDonChiTietResponse;
import com.example.datn.entity.*;
import com.example.datn.repository.DiaChiRepository;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.repository.NhanVienRepository;
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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ban-hang-tai-quay")
public class BanHangTaiQuayController {
    private final BanHangTaiQuayService banHangTaiQuayService;
    private final HoaDonService hoaDonService;
    private final SPCTRepo spctRepo;
    private final KhachHangRepo khachHangRepo;
    private final DiaChiRepository diaChiRepository;
    private final NhanVienRepository nhanVienRepository;

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
        model.addAttribute("listsKhachhang", khachHangRepo.findAll());

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


        List<HoaDonChiTiet> listHDCT = this.hoaDonService.timSanPhamChiTietTheoHoaDon(idHD);
        model.addAttribute("listHDCT", listHDCT);

        NhanVien nhanVien = nhanVienRepository.profileNhanVien();
        model.addAttribute("idNhanVienbanHang", nhanVien.getId());


        // Lấy tổng tiền từ service
        BigDecimal tongTien = banHangTaiQuayService.getTongTien(idHD);
        model.addAttribute("tongTien", tongTien);

        model.addAttribute("listsKhachhang", khachHangRepo.findAll());

        // Thêm ID hóa đơn được chọn vào model
        model.addAttribute("idHD", idHD);
        HoaDon hoaDon = hoaDonService.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại với ID: " + idHD));
        KhachHang khachHang =  hoaDon.getKhachHang();
        model.addAttribute("TTkhachHang",khachHang);
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(khachHang.getId());
        for (DiaChi diaChi:
                diaChiList) {
            if(diaChi.getTrangThai() == true){
                model.addAttribute("diachiMacDinh",diaChi);
            }
        }
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
    @PostMapping("/thanh-toan")
    public String thanhToan(
            @RequestParam("idHD") Long idhd,
            @RequestParam("phuongThucThanhToanKhiNhan") String phuongThucThanhToan
    ) {
        HoaDon hoaDon = hoaDonService.findById(idhd)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại với ID: " + idhd));
        NhanVien nhanVien = nhanVienRepository.profileNhanVien();
        hoaDon.setNhanVien(nhanVien);
        hoaDon.setHinhThucThanhToan(phuongThucThanhToan);
        System.out.println(phuongThucThanhToan);
        hoaDonService.save(hoaDon);
        return "redirect:/ban-hang-tai-quay/index";
    }
    @PostMapping("/addKH")
    public String addKHToHoaDonChiTiet(
            @RequestParam("idHD") Long idhd, // Sử dụng @RequestParam thay cho @ModelAttribute
            @RequestParam("idKH") Long idKh
    ) {
        // Tìm hóa đơn theo ID
        HoaDon hoaDon = hoaDonService.findById(idhd)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại với ID: " + idhd));

        // Tìm khách hàng theo ID
        KhachHang khachHang = khachHangRepo.findById(idKh)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại với ID: " + idKh));

        // Gán khách hàng vào hóa đơn
        hoaDon.setKhachHang(khachHang);

        // Lưu lại hóa đơn
        hoaDonService.save(hoaDon);

        // Chuyển hướng về trang chi tiết hóa đơn
        return "redirect:/ban-hang-tai-quay/hdct?idHD=" + idhd;
    }


    @PostMapping("tang-so-luong")
    public ResponseEntity<?> tangSoLuong(@RequestParam("idHoaDon") Long idHoaDon, @RequestParam("idSanPhamChiTiet") Long idSanPhamChiTiet) {
        try {
            hoaDonService.tangSoLuongSanPham(idHoaDon, idSanPhamChiTiet);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
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
            hoaDonService.deleteSPInHD( idSP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/ban-hang-tai-quay/hdct?idHD=" + idHD;
    }
    @PostMapping("dia-chi-mac-dinh")
    public String suaDiaChiMacDinh(@RequestParam("khachHangId") Integer khachHangId,
                                   @ModelAttribute("diaChiId") Integer diaChiId){
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(khachHangId);
        for (DiaChi diaChi: diaChiList) {
            if (diaChi.getId().equals(diaChiId)) {
                diaChi.setTrangThai(true); // Địa chỉ được chọn làm mặc định
            } else {
                diaChi.setTrangThai(false); // Các địa chỉ khác không phải là mặc định
            }
            diaChiRepository.save(diaChi); // Lưu vào database
        }
        return "redirect:/admin/khach-hang/from-sua/"+khachHangId;
    }
    @GetMapping("xoa/{id}")
    public String xoa(@RequestParam("khachHangId") Integer khachHangId,
                      @PathVariable("id") Integer id){
        diaChiRepository.deleteById(id);
        return "redirect:/admin/khach-hang/from-sua/"+khachHangId;
    }

}
