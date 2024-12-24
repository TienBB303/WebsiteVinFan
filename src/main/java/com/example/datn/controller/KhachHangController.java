package com.example.datn.controller;


import com.example.datn.entity.DiaChi;
import com.example.datn.entity.KhachHang;

import com.example.datn.repository.DiaChiRepository;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.sql.Date;
import java.time.LocalDate;
import java.util.List;


@Controller
@RequestMapping("/admin/khach-hang/")
public class KhachHangController {


    @Autowired
    private KhachHangRepo khachHangRepo;

    @Autowired
    EmailService emailService;

    @Autowired
    private DiaChiRepository diaChiRepository;



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
    public String fromThem() {
        return "/admin/khach-hang/add";
    }

    @PostMapping("add")
    public String add(@ModelAttribute("khachHang") KhachHang khachHang,
                      @RequestParam("tinhThanhPho") String tinhThanhPho,
                      @RequestParam("quanHuyen") String quanHuyen,
                      @RequestParam("xaPhuong") String xaPhuong,
                      @RequestParam("soNhaNgoDuong") String soNhaNgoDuong, Model model) {
        LocalDate currentDate = LocalDate.now();
        Date sqlDate = Date.valueOf(currentDate);
        khachHang.setNgayTao(sqlDate);
        khachHang.setTrangThai(true);

        // Lưu khách hàng vào cơ sở dữ liệu
        khachHangRepo.save(khachHang);

        // Tạo và lưu địa chỉ vào cơ sở dữ liệu
        DiaChi diaChi = new DiaChi();
        diaChi.setKhachHang(khachHang);
        diaChi.setTinhThanhPho(tinhThanhPho);
        diaChi.setQuanHuyen(quanHuyen);
        diaChi.setXaPhuong(xaPhuong);
        diaChi.setSoNhaNgoDuong(soNhaNgoDuong);
        diaChi.setTrangThai(true);
        diaChiRepository.save(diaChi);

        emailService.sendEmail(khachHang.getEmail(), "Tạo tài khoản thành công",
                khachHang.getEmail(), khachHang.getMatKhau(), "hentai.");
        return "redirect:/admin/khach-hang/index";
    }






    @GetMapping("from-sua/{id}")
    public String fromSua(Model model, @PathVariable("id") Integer id) {
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(id);  // lấy danh sách địa chỉ
        diaChiList.sort((d1, d2) -> Boolean.compare(
                d2.getTrangThai() != null ? d2.getTrangThai() : false,
                d1.getTrangThai() != null ? d1.getTrangThai() : false
        ));
        model.addAttribute("khachHang", khachHangRepo.findById(id).orElse(null));
        model.addAttribute("diaChiList", diaChiList);
        return "/admin/khach-hang/sua";  // trả về trang Thymeleaf
    }







    @PostMapping("update")
    public String sua(@ModelAttribute("khachHang") KhachHang khachHang){
        LocalDate currentDate = LocalDate.now();
        Date sqlDate = Date.valueOf(currentDate);
        khachHang.setNgaySua(sqlDate);
        khachHang.setId(khachHang.getId());
        // Lưu khách hàng vào cơ sở dữ liệu
        khachHangRepo.save(khachHang);
        // Tạo đối tượng địa chỉ và lưu vào cơ sở dữ liệu
        return "redirect:/admin/khach-hang/index";
    }

    //    @GetMapping("from-them/{id}")
//    public String fromThemDiaChi(Model model, @PathVariable("id") Integer id){
//        model.addAttribute("khachHang", khachHangRepo.findById(id).orElse(null));
//        return "/admin/khach-hang/add-dia-chi";
//    }
//
    @PostMapping("them-dia-chi")
    public String ThemDiaChi(@RequestParam("khachHangId") Integer khachHangId,
                             @RequestParam("tinhThanhPho") String tinhThanhPho,
                             @RequestParam("quanHuyen") String quanHuyen,
                             @RequestParam("xaPhuong") String xaPhuong,
                             @RequestParam("soNhaNgoDuong") String soNhaNgoDuong) {
        KhachHang khachHang = khachHangRepo.findById(khachHangId).orElseThrow(() -> new IllegalArgumentException("Khách hàng không tồn tại"));
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(khachHangId);
        for (DiaChi diaChi: diaChiList) {
            diaChi.setTrangThai(false); // Các địa chỉ khác không phải là mặc định
            diaChiRepository.save(diaChi); // Lưu vào database
        }
        DiaChi diaChi = new DiaChi();
        diaChi.setKhachHang(khachHang);  // Gán khách hàng đã có
        diaChi.setTinhThanhPho(tinhThanhPho);
        diaChi.setQuanHuyen(quanHuyen);
        diaChi.setXaPhuong(xaPhuong);
        diaChi.setSoNhaNgoDuong(soNhaNgoDuong);
        diaChi.setTrangThai(true);
        diaChiRepository.save(diaChi);

        return "redirect:/admin/khach-hang/from-sua/"+khachHangId;  // Chuyển hướng về trang sửa khách hàng
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
