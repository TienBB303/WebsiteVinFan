package com.example.datn.controller.BanHangTaiQuay;

import com.example.datn.dto.request.AddSPToHoaDonChiTietRequest;
import com.example.datn.entity.*;
import com.example.datn.entity.phieu_giam.PhieuGiam;
import com.example.datn.repository.*;
import com.example.datn.repository.DiaChiRepository;
import com.example.datn.repository.phieu_giam_repo.PhieuGiamRepo;
import com.example.datn.service.BanHangTaiQuay.BanHangTaiQuayService;
import com.example.datn.service.HoaDonService;
import com.example.datn.service.TrangThaiHoaDonService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Controller
@RequiredArgsConstructor
@RequestMapping("/ban-hang-tai-quay")
public class BanHangTaiQuayController {
    private final BanHangTaiQuayService banHangTaiQuayService;
    private final HoaDonService hoaDonService;
    private final TrangThaiHoaDonService trangThaiHoaDonService;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
    private final SPCTRepo spctRepo;
    private final KhachHangRepo khachHangRepo;
    private final DiaChiRepository diaChiRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PhieuGiamRepo phieuGiamRepo;
    private final HoaDonRepo hoaDonRepo;

    @GetMapping("/index")
    public String index(Model model) {
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
    public String viewHDCT(Model model, @RequestParam("idHD") Long idHD) {
        // Lấy danh sách mã giảm giá có loại phiếu là 1
        List<PhieuGiam> phieuGiamList = phieuGiamRepo.findByLoaiPhieuGiam(true); // true = loại phiếu là 1
        model.addAttribute("phieuGiamList", phieuGiamList);

        // Cập nhật tổng tiền trước khi lấy dữ liệu
        hoaDonService.updateTongTienHoaDon(idHD);

        // Hiển thị danh sách hóa đơn chờ
        List<HoaDon> listHoaDon = banHangTaiQuayService.findHoaDon();
        model.addAttribute("listHoaDon", listHoaDon);

        // Hiển thị danh sách sản phẩm add
        List<SanPhamChiTiet> listSPCTInHDCT = this.hoaDonService.getSPCTInHDCT();
        model.addAttribute("listSPCTInHDCT", listSPCTInHDCT);

        // Lấy danh sách chi tiết hóa đơn
        List<HoaDonChiTiet> listHDCT = this.hoaDonService.timSanPhamChiTietTheoHoaDon(idHD);
        model.addAttribute("listHDCT", listHDCT);

        NhanVien nhanVien = nhanVienRepository.profileNhanVien();
        model.addAttribute("idNhanVienbanHang", nhanVien.getId());

        // Lấy thông tin hóa đơn
        HoaDon hoaDon = hoaDonService.findById(idHD)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại với ID: " + idHD));

        // Lấy tổng tiền từ hóa đơn
        BigDecimal tongTien = hoaDon.getTongTien();
        model.addAttribute("tongTien", tongTien);

        // Tính tổng tiền sau giảm giá
        BigDecimal tongTienSauGiam = tongTien;
        if (hoaDon.getPhieuGiamGia() != null) {
            BigDecimal giaTriGiam = hoaDon.getPhieuGiamGia().getGiaTriGiam();
            tongTienSauGiam = tongTien.subtract(giaTriGiam).max(BigDecimal.ZERO); // Đảm bảo không âm
        }
        model.addAttribute("tongTienSauGiam", tongTienSauGiam);

        model.addAttribute("listsKhachhang", khachHangRepo.findAll());

        // Thêm ID hóa đơn được chọn vào model
        model.addAttribute("idHD", idHD);

        // Lấy thông tin khách hàng
        KhachHang khachHang = hoaDon.getKhachHang();
        model.addAttribute("TTkhachHang", khachHang);

        // Lấy địa chỉ mặc định
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(khachHang.getId());
        for (DiaChi diaChi : diaChiList) {
            if (diaChi.getTrangThai()) {
                model.addAttribute("diachiMacDinh", diaChi);
            }
        }

        return "admin/ban_hang_tai_quay/index";
    }

    @PostMapping("/addSPCT")
    public String addSPToHoaDonChiTiet(
            @ModelAttribute AddSPToHoaDonChiTietRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            // Lấy thông tin sản phẩm từ cơ sở dữ liệu
            SanPhamChiTiet sanPhamChiTiet = spctRepo.findById(request.getIdSP())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại với ID: " + request.getIdSP()));

            // Kiểm tra số lượng tồn kho
            if (sanPhamChiTiet.getSo_luong() < request.getSoLuong()) {
                redirectAttributes.addFlashAttribute("errorAdd", "Số lượng tồn kho không đủ cho sản phẩm: " + sanPhamChiTiet.getSanPham().getTen());
                return "redirect:/ban-hang-tai-quay/hdct?idHD=" + request.getIdHD();
            }

            // Thêm sản phẩm vào hóa đơn nếu số lượng hợp lệ
            hoaDonService.addSpToHoaDonChiTietRequestList(request);
            hoaDonService.timSanPhamChiTietTheoHoaDon(request.getIdHD());
            hoaDonService.updateTongTienHoaDon(request.getIdHD());

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Thêm sản phẩm thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorAdd", e.getMessage());
            System.out.println(e.getMessage());
        }
        return "redirect:/ban-hang-tai-quay/hdct?idHD=" + request.getIdHD();
    }

    @PostMapping("/thanh-toan")
    @Transactional
    public ResponseEntity<?> thanhToan(
            @RequestParam("idHD") Long idhd,
            @RequestParam("tongTienSauGiam") String tongTienSauGiamStr,
            @RequestParam(value = "idPhieuGiam", required = false) Integer idPhieuGiam,
            @RequestParam("phuongThucThanhToanKhiNhan") String phuongThucThanhToan,
            @RequestParam("tinhThanhPho") String tinhThanhPho,
            @RequestParam("soDienThoaiKhachHang") String soDienThoaiKhachHang,
            @RequestParam("quanHuyen") String quanHuyen,
            @RequestParam("xaPhuong") String xaPhuong,
            @RequestParam("chiTietDiaChi") String chitiet,
            @RequestParam("tienShip") String tienShipStr,
            @RequestParam("ghichu") String ghiChu,
            @RequestParam("tenKhangHang") String tenKhangHang) {

        try {
            // Kiểm tra hóa đơn
            HoaDon hoaDon = hoaDonService.findById(idhd)
                    .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại với ID: " + idhd));

            for (HoaDonChiTiet hdct : hoaDon.getHoaDonChiTietList()) {
                SanPhamChiTiet sp = hdct.getSanPhamChiTiet();
                if (hdct.getSoLuong() > sp.getSo_luong()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Không đủ số lượng sản phẩm: " + sp.getSanPham().getTen()));
                }
            }

            NhanVien nhanVien = nhanVienRepository.profileNhanVien();
            hoaDon.setNhanVien(nhanVien);

            // Cập nhật thông tin thanh toán
            String diaChiNguoiNhan = tinhThanhPho + "," + quanHuyen + "," + xaPhuong + "," + chitiet;

            hoaDon.setTenNguoiNhan(tenKhangHang);
            hoaDon.setDiaChi(diaChiNguoiNhan);
            hoaDon.setSdtNguoiNhan(soDienThoaiKhachHang);
            hoaDon.setGhiChu(ghiChu);
            hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaGiaoHang());
            hoaDon.setHinhThucThanhToan(phuongThucThanhToan);
            hoaDon.setNguoiTao(nhanVien.getTen());
            BigDecimal tienShip = chuyenChuoiSangBigDecimal(tienShipStr);
            hoaDon.setPhiVanChuyen(tienShip);
            hoaDon.setGhiChu(ghiChu);
            System.out.println("tien ship là : " + tienShipStr);

            // Lưu tổng tiền sau giảm
            String formatGiaTien = tongTienSauGiamStr.replaceAll("[^\\d]", "");
            BigDecimal tongTienSauGiam = new BigDecimal(formatGiaTien);
            hoaDon.setTongTienSauGiamGia(tongTienSauGiam);

            // Lưu ID phiếu giảm giá nếu có
            if (idPhieuGiam != null) {
                PhieuGiam phieuGiam = phieuGiamRepo.findById(idPhieuGiam)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá với ID: " + idPhieuGiam));
                hoaDon.setPhieuGiamGia(phieuGiam);
            }

            // Kiểm tra và trừ số lượng sản phẩm trong hóa đơn
            hoaDonService.truSoLuongSanPham(idhd);

            // Lưu hóa đơn vào cơ sở dữ liệu
            hoaDonService.save(hoaDon);

            // Tạo một bản ghi lịch sử cho HoaDon đã được xác nhận
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaGiaoHang());
            lichSuHoaDon.setNgayTao(LocalDate.now());
            lichSuHoaDon.setNguoiTao(nhanVien.getTen());
            lichSuHoaDonRepo.save(lichSuHoaDon);

            return ResponseEntity.ok(Map.of("message", "Thanh toán thành công!"));

        } catch (RuntimeException e) {
            // Rollback sẽ xảy ra tự động nhờ @Transactional
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
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

        DiaChi diaChi = diaChiRepository.DiaChimacDinhvsfindByKhachHangId(Math.toIntExact(idKh));
        String diaChiHoaDon = "";
        if (diaChi != null) {
            diaChiHoaDon = diaChi.getTinhThanhPho() +
                    "," + diaChi.getQuanHuyen() + ","
                    + diaChi.getXaPhuong() + ","
                    + diaChi.getSoNhaNgoDuong();
        } else {
            System.out.println("Không tìm thấy địa chỉ mặc định. Sử dụng địa chỉ trống.");
            diaChiHoaDon = "Địa chỉ không xác định"; // Giá trị mặc định (hoặc để trống "")
        }
        System.out.println("Địa chỉ nhận là: " + diaChiHoaDon);

        hoaDon.setDiaChi(diaChiHoaDon);

        // Gán khách hàng vào hóa đơn
        hoaDon.setKhachHang(khachHang);

        // Lưu lại hóa đơn
        hoaDonService.save(hoaDon);

        // Chuyển hướng về trang chi tiết hóa đơn
        return "redirect:/ban-hang-tai-quay/hdct?idHD=" + idhd;
    }


    @PostMapping("tang-so-luong")
    public String tangSoLuong(@RequestParam("idHoaDon") Long idHoaDon,
                              @RequestParam("idSanPhamChiTiet") Long idSanPhamChiTiet,
                              RedirectAttributes redirectAttributes) {
        try {
            hoaDonService.tangSoLuongSanPham(idHoaDon, idSanPhamChiTiet);
            hoaDonService.updateTongTienHoaDon(idHoaDon);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("messageTSL", "Cập nhật số lượng sản phẩm thành công!");
        } catch (RuntimeException e) {
            // Thêm thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessageTSL", e.getMessage());
        }

        return "redirect:/ban-hang-tai-quay/hdct?idHD=" + idHoaDon;
    }


    @PostMapping("giam-so-luong")
    public String giamSoLuong(@RequestParam("idHoaDon") Long idHoaDon,
                              @RequestParam("idSanPhamChiTiet") Long idSanPhamChiTiet) {
        try {
            hoaDonService.giamSoLuongSanPham(idHoaDon, idSanPhamChiTiet);
            hoaDonService.updateTongTienHoaDon(idHoaDon);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return "redirect:/ban-hang-tai-quay/hdct?idHD=" + idHoaDon;
    }

    @PostMapping("/tao-hoa-don")
    public String taoHoaDon(RedirectAttributes redirectAttributes) {
        try {
            HoaDon hoaDon = new HoaDon();
            NhanVien nhanVien = nhanVienRepository.profileNhanVien();
            hoaDon.setNhanVien(nhanVien);
            banHangTaiQuayService.taoHoaDonCho(hoaDon);
            redirectAttributes.addFlashAttribute("message", "Tạo hóa đơn thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorTHD", e.getMessage());
        }
        return "redirect:/ban-hang-tai-quay/index";
    }

    @PostMapping("/xoa")
    public String deleteChiTiet(
            @RequestParam("idHD") Long idHD,
            @RequestParam("idSP") Long idSP) {
        try {
            hoaDonService.deleteSPInHD(idSP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/ban-hang-tai-quay/hdct?idHD=" + idHD;
    }

    @PostMapping("dia-chi-mac-dinh")
    public String suaDiaChiMacDinh(@RequestParam("khachHangId") Integer khachHangId,
                                   @ModelAttribute("diaChiId") Integer diaChiId) {
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(khachHangId);
        for (DiaChi diaChi : diaChiList) {
            if (diaChi.getId().equals(diaChiId)) {
                diaChi.setTrangThai(true); // Địa chỉ được chọn làm mặc định
            } else {
                diaChi.setTrangThai(false); // Các địa chỉ khác không phải là mặc định
            }
            diaChiRepository.save(diaChi); // Lưu vào database
        }
        return "redirect:/admin/khach-hang/from-sua/" + khachHangId;
    }

    @GetMapping("xoa/{id}")
    public String xoa(@RequestParam("khachHangId") Integer khachHangId,
                      @PathVariable("id") Integer id) {
        diaChiRepository.deleteById(id);
        return "redirect:/admin/khach-hang/from-sua/" + khachHangId;
    }

//    TienBB
//    @GetMapping("/xoa-hoa-don/{id}")
//    public String xoaHoaDonTaiQuay(@PathVariable("id") Long idHoaDon) {
//        HoaDon hd = hoaDonRepo.timKiemHoaDonByID(idHoaDon);
//        if (hd == null) {
//            System.out.println("Không tồn tại hóa đơn");
//            return "redirect:/ban-hang-tai-quay/index";
//        }
//        try {
//            hd.setKhachHang(null);
//            hd.setNhanVien(null);
//            hd.setPhieuGiamGia(null);
//            hoaDonRepo.save(hd);
//
//            hoaDonRepo.delete(hd); // Xóa hóa đơn
//        } catch (Exception e) {
//            System.out.println("Lỗi khi xóa: " + e.getMessage());
//        }
//        return "redirect:/ban-hang-tai-quay/index";
//    }


    @GetMapping("/xoa-hoa-don/{id}")
    public String xoaHoaDonTaiQuay(@PathVariable("id") Long idHoaDon) {
        HoaDon hoaDon = hoaDonService.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại với ID: " + idHoaDon));
        try {
            NhanVien nhanVien = nhanVienRepository.profileNhanVien();

            // Tạo một bản ghi lịch sử cho HoaDon đã được xác nhận
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getHuy());
            lichSuHoaDon.setNgayTao(LocalDate.now());
            lichSuHoaDon.setNguoiTao(nhanVien.getTen());
            // Lưu lịch sử hóa đơn
            lichSuHoaDonRepo.save(lichSuHoaDon);

            hoaDon.setNhanVien(nhanVien);
            hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getHuy());
            hoaDonRepo.save(hoaDon);
        } catch (Exception e) {
            System.out.println("Lỗi khi xóa: " + e.getMessage());
        }
        return "redirect:/ban-hang-tai-quay/index";
    }
    private BigDecimal chuyenChuoiSangBigDecimal(String tienStr) {
        if (tienStr == null || tienStr.isEmpty()) {
            return BigDecimal.ZERO;
        }
        // Loại bỏ ký tự không phải số
        String chiSoTien = tienStr.replaceAll("[^\\d]", "");
        if (chiSoTien.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(chiSoTien);
    }
}