package com.example.datn.controller;

import com.example.datn.dto.request.AddSPToHoaDonChiTietRequest;
import com.example.datn.dto.request.SearchSanPhamChiTietRequest;
import com.example.datn.dto.request.TrangThaiHoaDonRequest;
import com.example.datn.dto.request.UpdateQuantityRequest;
import com.example.datn.dto.response.LichSuThanhToanResponse;
import com.example.datn.dto.response.ListSanPhamInHoaDonChiTietResponse;

import com.example.datn.dto.response.ListSpNewInHoaDonResponse;
import com.example.datn.dto.response.PggInHoaDonResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.LichSuHoaDon;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.HoaDonChiTietRepo;
import com.example.datn.repository.HoaDonRepo;
import com.example.datn.repository.LichSuHoaDonRepo;
import com.example.datn.repository.SPCTRepo;
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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hoa-don")
public class HoaDonController {
    private final HoaDonService hoaDonService;
    private final TrangThaiHoaDonService trangThaiHoaDonService;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SPCTRepo spctRepo;

    @Autowired
    HoaDonRepo hoaDonRespo;

    PggInHoaDonResponse pggInHoaDonResponse;

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


        //Lấy thông tin sp in hoa don
//        List<ListSpNewInHoaDonResponse> list = this.hoaDonService.getSanPhamInHoaDon();
//        model.addAttribute("listSPInHoaDon", list);

        //Lấy thông tin sp theo id hóa đơn
        List<ListSanPhamInHoaDonChiTietResponse> listHDCT = this.hoaDonService.getSanPhamCTByHoaDonId(id);
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


    @PostMapping("/cho-xac-nhan")
    public String choXacNhan(@ModelAttribute("id") long id) {
        // Tìm kiếm HoaDon dựa trên id được nhận từ yêu cầu
        Optional<HoaDon> hoaDonOptional = hoaDonService.findById(id);

        if (hoaDonOptional.isPresent()) {
            HoaDon hoaDon = hoaDonOptional.get();

            // Cập nhật trạng thái của HoaDon thành "Đã Xác Nhận"
            hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getChoXacNhan());
            hoaDonService.save(hoaDon);

            // Tạo một bản ghi lịch sử cho HoaDon đã được xác nhận
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHoaDon(hoaDon);
            lichSuHoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getChoXacNhan());
            lichSuHoaDon.setNgayTao(LocalDate.now());
            lichSuHoaDonRepo.save(lichSuHoaDon);
        }

        // Chuyển hướng người dùng đến trang chi tiết của HoaDon
        return "redirect:/hoa-don/detail?id=" + id; // Chuyển hướng với tham số id
    }

    @PostMapping("/xac-nhan")
    public String xacNhan(
            @ModelAttribute("id") long id
    ) {
        hoaDonService.xacNhanHoaDon(id);
        hoaDonService.updateTongTienHoaDon();
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

    @PostMapping("/update-quantity")
    public ResponseEntity<Map<String, Object>> updateQuantity(@RequestBody UpdateQuantityRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Giả sử bạn có một service để xử lý việc cập nhật số lượng
        boolean isUpdated = hoaDonService.updateQuantity(request.getItemId(), request.getQuantity());

        if (isUpdated) {
            response.put("success", true);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
