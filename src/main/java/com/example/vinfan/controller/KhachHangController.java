package com.example.vinfan.controller;

import com.example.vinfan.entity.DiaChi;
import com.example.vinfan.entity.KhachHang;
import com.example.vinfan.repository.DiaChiRepository;
import com.example.vinfan.repository.KhachHangRepo;

import com.example.vinfan.service.EmailService;
import com.example.vinfan.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;


@Controller
@RequestMapping("/admin/khach-hang/")
public class KhachHangController {


    @Autowired
    private KhachHangRepo khachHangRepo;

    @Autowired
    EmailService emailService;

    @Autowired
    private DiaChiRepository diaChiRepository;

    @Autowired
    private KhachHangService khachHangService;

    @GetMapping("/index")
    public String loadTable(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "trangThai", required = false) Boolean trangThai,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        if (page < 0) {
            page = 0;
        }

        PageRequest pageable = PageRequest.of(page, size);
        Page<KhachHang> khachHangPage;

        khachHangPage = khachHangRepo.searchKhachHang(keyword, trangThai, startDate, endDate, pageable);
        model.addAttribute("listsKhachhang", khachHangPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", khachHangPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "/admin/khach-hang/index";
    }


    @GetMapping("from-them")
    public String fromThem(Model model) {
        return "/admin/khach-hang/add";
    }

    @PostMapping("add")
    public String add(@ModelAttribute("khachHang") KhachHang khachHang,
                      @RequestParam("tinhThanhPho") String tinhThanhPho,
                      @RequestParam("quanHuyen") String quanHuyen,
                      @RequestParam("xaPhuong") String xaPhuong,
                      @RequestParam("soNhaNgoDuong") String soNhaNgoDuong, Model model) {

        // Kiểm tra các trường bắt buộc không được để trống
//        if (khachHang.getMa().isEmpty()
//                ) {
//            model.addAttribute("mavali", "Không được để trống");
//            return null;
//        }
        if (khachHang.getTen().isEmpty()
        ) {
            model.addAttribute("tenvali", "Không được để trống");
            return null;
        }

        if (khachHang.getEmail().isEmpty()) {
            model.addAttribute("emailvali", "Không được để trống");
            return null;
        }
        if (khachHang.getMatKhau().isEmpty()) {
            model.addAttribute("matkhauvali", "Không được để trống");
            return null;
        }if (khachHang.getGioiTinh().isEmpty() ) {
            model.addAttribute("gioitinhvali", "Không được để trống");
            return null;
        }
        if (khachHang.getSoDienThoai().isEmpty() ) {
            model.addAttribute("sdtvali", "Không được để trống");
            return null;
        }

//        // Kiểm tra trùng lặp số điện thoại
//        KhachHang existingKhachHangBySoDienThoai = khachHangRepo.findBySoDienThoai(khachHang.getSoDienThoai());
//        if (existingKhachHangBySoDienThoai != null) {
//            model.addAttribute("trungsdt", "Số điện thoại đã tồn tại");
//            return null;
//        }
////
//        // Kiểm tra trùng lặp email
//        KhachHang existingKhachHangByEmail = khachHangRepo.findByEmail(khachHang.getEmail());
//        if (existingKhachHangByEmail != null) {
//            model.addAttribute("trungemail", "Email đã tồn tại");
//            return null;
//        }

        LocalDate currentDate = LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(currentDate);
        khachHang.setNgayTao(sqlDate);

        // Lưu khách hàng vào cơ sở dữ liệu
        khachHangRepo.save(khachHang);

        // Tạo và lưu địa chỉ vào cơ sở dữ liệu
        DiaChi diaChi = new DiaChi();
        diaChi.setKhachHang(khachHang);
        diaChi.setTinhThanhPho(tinhThanhPho);
        diaChi.setQuanHuyen(quanHuyen);
        diaChi.setXaPhuong(xaPhuong);
        diaChi.setSoNhaNgoDuong(soNhaNgoDuong);
        diaChiRepository.save(diaChi);

        emailService.sendEmail(khachHang.getEmail(), "Tạo tài khoản thành công",
                khachHang.getEmail(), khachHang.getMatKhau(), "hentai.");
        return "redirect:/admin/khach-hang/index";
    }






    @GetMapping("from-sua/{id}")
    public String fromSua(Model model, @PathVariable("id") Integer id) {
        model.addAttribute("khachHang", khachHangRepo.findById(id).orElse(null));
        model.addAttribute("diaChi", (DiaChi) diaChiRepository.findByKhachHangId(id));
        return "/admin/khach-hang/sua";
    }



    @PostMapping("update")
    public String sua(@ModelAttribute("khachHang") KhachHang khachHang,
                      @RequestParam("tinhThanhPho") String tinhThanhPho,
                      @RequestParam("quanHuyen") String quanHuyen,
                      @RequestParam("xaPhuong") String xaPhuong,
                      @RequestParam("soNhaNgoDuong") String soNhaNgoDuong){
        LocalDate currentDate = LocalDate.now();
        java.sql.Date sqlDate = Date.valueOf(currentDate);
        khachHang.setNgaySua(sqlDate);

        // Lưu khách hàng vào cơ sở dữ liệu
        khachHangRepo.save(khachHang);
        // Tạo đối tượng địa chỉ và lưu vào cơ sở dữ liệu
        DiaChi diaChi = new DiaChi();
        diaChi.setKhachHang(khachHang);
        diaChi.setTinhThanhPho(tinhThanhPho);
        diaChi.setQuanHuyen(quanHuyen);
        diaChi.setXaPhuong(xaPhuong);
        diaChi.setSoNhaNgoDuong(soNhaNgoDuong);
        diaChiRepository.save(diaChi);
        return "redirect:/admin/khach-hang/index";
    }

    @PostMapping("update-status")
    public ResponseEntity<String> updateStatus(@RequestBody Map<String, Object> payload) {
        Integer id = ((Number) payload.get("id")).intValue();
        Boolean trangThai = (Boolean) payload.get("trangThai");

        try {
            khachHangService.updateTrangThai(id, trangThai);
            return ResponseEntity.ok("Cập nhật trạng thái thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật trạng thái");
        }
    }


}
