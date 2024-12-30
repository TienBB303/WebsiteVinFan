package com.example.datn.controller;




import com.example.datn.entity.ChucVu;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.ChucVuRepository;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.service.CloudinaryService;
import com.example.datn.service.nhan_vien_service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;


import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/nhan-vien/")
public class NhanVienController {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    NhanVienService nhanVienService;

    @Autowired
    private ChucVuRepository chucVuRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/index")
    public String loadTable(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "trang_thai", defaultValue = "") String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model){
        if (page < 0) {
            page = 0;
        }
        Boolean trang_thai = null;
        if ("1".equals(trangThai.trim())) {
            trang_thai = true;
        } else if ("0".equals(trangThai.trim())) {
            trang_thai = false;
        }
        PageRequest pageable = PageRequest.of(page, size);
        Page<NhanVien> nhanVienPage = nhanVienService.search(keyword.trim(), trang_thai, pageable);
        model.addAttribute("listsNhanVien", nhanVienPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("trang_thai", trang_thai);
        return "admin/nhan-vien/index";
    }

    @GetMapping("/from-them")
    public String formThemNhanVien(Model model) {
        NhanVien nhanVien = new NhanVien();
        List<ChucVu> chucVuList = chucVuRepository.findAll();
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("chucVuList", chucVuList);
        return "admin/nhan-vien/add";
    }

    @PostMapping("/add")
    public String addNhanVien(@ModelAttribute NhanVien nhanVien, @RequestParam("file") MultipartFile file,Model model) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Chưa có tệp hình ảnh được chọn.");
            }
            Map uploadResult = cloudinaryService.upload(file);
            String imageUrl = (String) uploadResult.get("url");
            nhanVien.setHinhAnh(imageUrl);

            LocalDate currentDate = LocalDate.now();
            Date sqlDate = Date.valueOf(currentDate);
            nhanVien.setNgayTao(sqlDate);
            nhanVien.setTrangThai(true);

            nhanVienRepository.save(nhanVien);

            return "redirect:/admin/nhan-vien/index";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/admin/nhan-vien/add?error=upload_failed";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return "redirect:/admin/nhan-vien/add?error=" + e.getMessage();
        }
    }
    @RestController
    @RequestMapping("/api")
    public class NhanVienAPIController {

        @Autowired
        private NhanVienRepository nhanVienRepository;

        @PostMapping("/check-email")
        public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody Map<String, String> request) {
            String email = request.get("email");
            boolean exists = nhanVienRepository.findByEmail(email) != null;
            return ResponseEntity.ok(Collections.singletonMap("exists", exists));
        }
    }


    @GetMapping("/from-sua/{id}")
    public String formSuaNhanVien(@PathVariable("id") Integer id, Model model) {
        NhanVien nhanVien = nhanVienRepository.findById(id).orElse(null);
        List<ChucVu> chucVuList = chucVuRepository.findAll();
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("chucVuList", chucVuList);
        model.addAttribute("nowday", LocalDate.now());
        return "admin/nhan-vien/sua";
    }
    @PostMapping("/update")
    public String suaNhanVien(NhanVien nhanVien) {
        LocalDate currentDate = LocalDate.now(); // Lấy ngày hiện tại
        Date sqlDate = Date.valueOf(currentDate); // Chuyển đổi LocalDate sang Date
        nhanVien.setNgaySua(sqlDate);

        // Kiểm tra và thiết lập các giá trị mặc định nếu cần
        if (nhanVien.getGioiTinh() == null) {
            nhanVien.setGioiTinh(false); // Hoặc một giá trị mặc định khác
        }

        nhanVienRepository.save(nhanVien);
        return "redirect:/admin/nhan-vien/index";
    }


}
