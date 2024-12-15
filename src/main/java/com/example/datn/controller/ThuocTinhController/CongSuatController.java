package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.thuoc_tinh.ChatLieuKhung;
import com.example.datn.entity.thuoc_tinh.CongSuat;
import com.example.datn.repository.ThuocTinhRepo.CongSuatRepo;
import com.example.datn.service.thuoc_tinh_service.CongSuatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/thuoc-tinh/cong-suat")
public class CongSuatController {
    @Autowired
    CongSuatRepo congSuatRepo;

    @Autowired
    CongSuatService congSuatService;

    @ModelAttribute("listCongSuat")
    public List<CongSuat> listCongSuat() {
        return congSuatRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_cong_suat", defaultValue = "") String ten_cong_suat,
                          @RequestParam(value = "trang_thai", defaultValue = "") String trang_thaiStr,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "5") int size,
                          Model model) {
        Boolean trang_thai = null;
        if ("1".equals(trang_thaiStr)) {
            trang_thai = true;
        } else if ("0".equals(trang_thaiStr)) {
            trang_thai = false;
        }
        Page<CongSuat> searchPage = congSuatService.search(ten_cong_suat, trang_thai, PageRequest.of(page, size));
        model.addAttribute("listCS", searchPage);
        model.addAttribute("ten_cong_suat", ten_cong_suat);
        model.addAttribute("trang_thai", trang_thaiStr);
        return "admin/thuoc_tinh/cong_suat";
    }

    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        CongSuat congSuat = congSuatRepo.findById(id).orElse(null);
        if (congSuat != null) {
            if (congSuat.getTrang_thai() == true) {
                congSuat.setTrang_thai(false);
            } else if (congSuat.getTrang_thai() == false) {
                congSuat.setTrang_thai(true);
            }
            congSuatRepo.save(congSuat);
            return ResponseEntity.ok("Công suất thay đổi trạng thái thành công.");
        } else {
            return ResponseEntity.badRequest().body("Không có công suất hiện tại.");
        }
    }

    @PostMapping("/addCongSuat")
    public ResponseEntity<?> themMoi(@RequestParam("ten_cong_suat") String tenCongSuat) {
        if (tenCongSuat == null || tenCongSuat.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên công suất không được để trống.");
        }
        Optional<CongSuat> checkTonTai = congSuatRepo.findByTen(tenCongSuat.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại công suất.");
        } else {
            CongSuat congSuat = new CongSuat();
            congSuat.setTen(tenCongSuat.trim());
            congSuat.setTrang_thai(true);
            congSuatRepo.save(congSuat);
            return ResponseEntity.ok("Công suất thêm mới thành công.");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> thongTin(@PathVariable("id") Integer id) {
        Optional<CongSuat> congSuat = congSuatRepo.findById(id);
        if (congSuat.isPresent()) {
            System.out.println("Found CongSuat: " + congSuat.get());
            return ResponseEntity.ok(congSuat.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại công suất.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCongSuat(@RequestParam("id") Integer id, @RequestParam("ten_cong_suat_update") String tenCongSuat) {
        if (tenCongSuat == null || tenCongSuat.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên công suất không được để trống.");
        }

        Optional<CongSuat> checkTonTai = congSuatRepo.findByTen(tenCongSuat.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại công suất.");
        }
        Optional<CongSuat> congSuatOptional = congSuatRepo.findById(id);
        if (congSuatOptional.isPresent()) {
            CongSuat congSuat = congSuatOptional.get();
            congSuat.setTen(tenCongSuat);
            congSuatRepo.save(congSuat);
            return ResponseEntity.ok("Cập nhật công suất thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy công suất.");
        }
    }
}
