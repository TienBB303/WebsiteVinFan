package com.example.datn.controller;

import com.example.datn.dto.request.AddSPToHoaDonChiTietRequest;
import com.example.datn.dto.request.TrangThaiHoaDonRequest;
import com.example.datn.dto.response.LichSuThanhToanResponse;
import com.example.datn.dto.response.ListSanPhamInHoaDonChiTietResponse;

import com.example.datn.dto.response.ListSpNewInHoaDonResponse;
import com.example.datn.dto.response.PggInHoaDonResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.LichSuHoaDon;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.HoaDonChiTietRepo;
import com.example.datn.repository.HoaDonRepo;
import com.example.datn.repository.LichSuHoaDonRepo;
import com.example.datn.service.HoaDonService;
import com.example.datn.service.TrangThaiHoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hoa-don")
public class HoaDonController {
    private final HoaDonService hoaDonService;
    private final TrangThaiHoaDonService trangThaiHoaDonService;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;

    @Autowired
    HoaDonRepo hoaDonRespo;


    @GetMapping("index")
    public String index(@RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "5") int size,
                        @RequestParam(name = "query", defaultValue = "") String query,
                        @RequestParam(name = "trangThai", required = false) Integer trangThai,
                        @RequestParam(name = "method", defaultValue = "all") String method,
                        @RequestParam(name = "startDate", required = false) String startDate,
                        @RequestParam(name = "endDate", required = false) String endDate,
                        Model model) {
        Page<HoaDon> list;

        LocalDate start = null;
        LocalDate end = null;

        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDate.parse(endDate);
            }
        } catch (DateTimeParseException e) {
            // Xử lý lỗi định dạng ngày nếu cần (ví dụ: ghi log)
            e.printStackTrace();
        }

        // Logic lọc
        if (query.isEmpty() && trangThai == null && method.equals("all") && start == null && end == null) {
            list = hoaDonService.findHoaDonAndSortDay(page, size);
        } else if (!query.isEmpty()) {
            list = hoaDonService.searchHoaDon("%" + query + "%", PageRequest.of(page, size));
        } else if (trangThai != null) {
            list = hoaDonService.getAllHoaDonByTrangThai(trangThai, PageRequest.of(page, size));
        } else if (start != null && end != null) {
            list = hoaDonService.getHoaDonByDateRange(start, end, PageRequest.of(page, size));
        } else if (!method.equals("all")) {
            boolean isOnline = method.equals("1");
            list = hoaDonService.getAllHoaDonByLoaiHoaDon(isOnline, PageRequest.of(page, size));
        } else {
            list = hoaDonService.findHoaDonAndSortDay(page, size);
        }

        // Gán dữ liệu vào Model
        model.addAttribute("list", list);
        model.addAttribute("query", query);
        model.addAttribute("status", trangThai != null ? trangThai : 6);
        model.addAttribute("method", method);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        TrangThaiHoaDonRequest trangThaiHoaDon = trangThaiHoaDonService.getTrangThaiHoaDonRequest();
        model.addAttribute("trangThaiHoaDon", trangThaiHoaDon);

        return "/admin/hoa_don/index";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam long id, Model model) {
        //Lấy thông tin hóa đơn
        Optional<HoaDon> hoaDonOptional = hoaDonService.findById(id);
        HoaDon hoaDon = new HoaDon();
        if (hoaDonOptional.isPresent()) {
            hoaDon = hoaDonOptional.get();
        }
        model.addAttribute("hoaDon", hoaDon);

        String chuoi = hoaDon.getDiaChi();
        if (chuoi == null || chuoi.isEmpty()) {
            // Cung cấp giá trị mặc định nếu chuoi là null hoặc rỗng
            chuoi = "N/A,N/A,N/A,N/A";
        }

        String[] mang = chuoi.split(",");
        model.addAttribute("tinh", mang.length > 0 ? mang[0] : "N/A");
        model.addAttribute("huyen", mang.length > 1 ? mang[1] : "N/A");
        model.addAttribute("xa", mang.length > 2 ? mang[2] : "N/A");
        model.addAttribute("chitietdiachi", mang.length > 3 ? mang[3] : "N/A");



        //Lấy thông tin sp in hoa don
        List<ListSpNewInHoaDonResponse> list = this.hoaDonService.getSanPhamInHoaDon();
        model.addAttribute("listSPInHoaDon", list);

        List<HoaDonChiTiet> listHDCT = this.hoaDonService.timSanPhamChiTietTheoHoaDon(id);
        model.addAttribute("listHDCT", listHDCT);

        //Lấy thông tin sp in hoaDonChiTiet
        List<SanPhamChiTiet> listSPCTInHDCT = this.hoaDonService.getSPCTInHDCT();
        model.addAttribute("listSPCTInHDCT", listSPCTInHDCT);

        //Lấy thông tin thanh toan theo id hóa đơn
        LichSuThanhToanResponse lichSuThanhToanResponse = this.hoaDonService.getLSTTByHoaDonId(id);
        model.addAttribute("listLSTT", lichSuThanhToanResponse);

        //Lấy thông tin pgg theo id hóa đơn
        PggInHoaDonResponse hoaDonPGG = hoaDonService.getPGGbyHoaDonId(id);
        model.addAttribute("listPGG", hoaDonPGG);

        //Lấy thông tin lịch sử hóa đơn theo id hóa đơn
        List<LichSuHoaDon> lichSuHoaDonList = lichSuHoaDonRepo.findLichSuHoaDonByIdHoaDon(id);
        model.addAttribute("listHistory", lichSuHoaDonList);

        return "/admin/hoa_don/detail";
    }

    @PostMapping("/addSPCT")
    public String addSPToHoaDonChiTiet(
            @ModelAttribute AddSPToHoaDonChiTietRequest request
    ) {
        System.out.println("Giá là" + request.getGia());
        System.out.println("sl là" + request.getSoLuong());
        try {
            hoaDonService.addSpToHoaDonChiTietRequestList(request); // Gọi service để thêm sản phẩm vào hóa đơn
            System.out.println("Thêm sản phẩm thành công!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/hoa-don/detail?id=" + request.getIdHD();
    }



    @PostMapping("/xac-nhan")
    public String xacNhan(
            @ModelAttribute("id") long id
    ) {
        // Tìm kiếm HoaDon dựa trên id được nhận từ yêu cầu
        Optional<HoaDon> hoaDonOptional = hoaDonService.findById(id);

        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();

            // Cập nhật trạng thái của HoaDon thành "Đã Xác Nhận"
            hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaXacNhan());
            hoaDonService.save(hoaDon);
            hoaDonService.updateTongTienHoaDon();
            hoaDonService.truSoLuongSanPham(id);
            // Tạo một bản ghi lịch sử cho HoaDon đã được xác nhận
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaXacNhan());
            lichSuHoaDon.setNgayTao(LocalDate.now());

            lichSuHoaDonRepo.save(lichSuHoaDon);
        }

        // Chuyển hướng người dùng đến trang chi tiết của HoaDon
        return "redirect:/hoa-don/detail?id=" + id; // Chuyển hướng với tham số id
    }

    @PostMapping("/giao-hang")
    public String dangGiaoHang(@ModelAttribute("id") long id) {
        // Tìm kiếm HoaDon dựa trên id được nhận từ yêu cầu
        Optional<HoaDon> hoaDonOptional = hoaDonService.findById(id);

        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();

            // Cập nhật trạng thái của HoaDon thành "Đã Xác Nhận"
            hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDangGiaoHang());
            hoaDonService.save(hoaDon);

            // Tạo một bản ghi lịch sử cho HoaDon đã được xác nhận
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDangGiaoHang());
            lichSuHoaDon.setNgayTao(LocalDate.now());

            lichSuHoaDonRepo.save(lichSuHoaDon);
        }

        // Chuyển hướng người dùng đến trang chi tiết của HoaDon
        return "redirect:/hoa-don/detail?id=" + id; // Chuyển hướng với tham số id
    }


    @PostMapping("/hoan-thanh")
    public String hoanThanh(@ModelAttribute("id") long id) {
        // Tìm kiếm HoaDon dựa trên id được nhận từ yêu cầu
        Optional<HoaDon> hoaDonOptional = hoaDonService.findById(id);
        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();

            // Cập nhật trạng thái của HoaDon thành "Đã Xác Nhận"
            hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaGiaoHang());
            hoaDonService.save(hoaDon);

            // Tạo một bản ghi lịch sử cho HoaDon đã được xác nhận
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getDaGiaoHang());
            lichSuHoaDon.setNgayTao(LocalDate.now());

            lichSuHoaDonRepo.save(lichSuHoaDon);
            hoaDonService.truSoLuongSanPham(id);
        }
        // Chuyển hướng người dùng đến trang chi tiết của HoaDon
        return "redirect:/hoa-don/detail?id=" + id; // Chuyển hướng với tham số id
    }

    @PostMapping("/huy")
    public String huy(@ModelAttribute("id") long id) {
        hoaDonService.huyHoaDon(id);

        // Chuyển hướng người dùng đến trang chi tiết của HoaDon
        return "redirect:/hoa-don/detail?id=" + id; // Chuyển hướng với tham số id
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
        return "redirect:/hoa-don/detail?id=" + idHD;
    }

    @PostMapping("tang-so-luong")
    public ResponseEntity<?> tangSoLuong(@RequestParam("idHoaDon") Long idHoaDon,
                                         @RequestParam("idSanPhamChiTiet") Long idSanPhamChiTiet
                                         ) {
        try {
            hoaDonService.tangSoLuongSanPham(idHoaDon, idSanPhamChiTiet);
            hoaDonService.updateTongTienHoaDon();
            return ResponseEntity.ok().body("Thêm thành công");
        } catch (RuntimeException e) {
            // Trả về thông báo lỗi
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("giam-so-luong")
    public ResponseEntity<?> giamSoLuong(@RequestParam("idHoaDon") Long idHoaDon,
                                         @RequestParam("idSanPhamChiTiet") Long idSanPhamChiTiet) {
        try {
            hoaDonService.giamSoLuongSanPham(idHoaDon, idSanPhamChiTiet);
            hoaDonService.updateTongTienHoaDon();
            return ResponseEntity.ok("Giảm số lượng thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PostMapping("/sua-thong-tin")
    public String updateThongTinNguoiNhan(@RequestParam("tenMoi") String tenNguoiNhanMoi,
                                          @RequestParam("soDienThoaiMoi") String sdtNguoiNhanMoi,
                                          @RequestParam("tinhThanhPho") String tinhThanhPho,
                                          @RequestParam("quanHuyen") String quanHuyen,
                                          @RequestParam("xaPhuong") String xaPhuong,
                                          @RequestParam("soNhaNgoDuong") String soNhaNgoDuong,
                                          @RequestParam("id") long id) {
        Optional<HoaDon> hoaDonOpt = hoaDonRespo.findById(id);
        String diaChiMoi = tinhThanhPho + "," + quanHuyen + "," + xaPhuong + "," + soNhaNgoDuong;
        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            hoaDon.setTenNguoiNhan(tenNguoiNhanMoi);
            hoaDon.setSdtNguoiNhan(sdtNguoiNhanMoi);
            hoaDon.setDiaChi(diaChiMoi);
            hoaDonRespo.save(hoaDon);
        }
        return "redirect:/hoa-don/detail?id=" + id;
    }

}
