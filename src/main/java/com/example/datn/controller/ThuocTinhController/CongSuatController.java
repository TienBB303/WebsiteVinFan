package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.NhanVien;
import com.example.datn.entity.thuoc_tinh.ChatLieuKhung;
import com.example.datn.entity.thuoc_tinh.CongSuat;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.repository.ThuocTinhRepo.CongSuatRepo;
import com.example.datn.service.thuoc_tinh_service.CongSuatService;
import org.eclipse.tags.shaded.org.apache.xpath.operations.Bool;
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

    @Autowired
    NhanVienRepository nhanVienRepository;
    @ModelAttribute("listCongSuat")
    public List<CongSuat> listCongSuat() {
        return congSuatRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_cong_suat", defaultValue = "") String ten_cong_suat,
                          @RequestParam(value = "trang_thai", defaultValue = "") Boolean trang_thai,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "5") int size,
                          Model model) {
        Page<CongSuat> searchPage = congSuatService.search(ten_cong_suat, trang_thai, PageRequest.of(page, size));
        model.addAttribute("listCS", searchPage);
        model.addAttribute("ten_cong_suat", ten_cong_suat);
        model.addAttribute("trang_thai", trang_thai != null ? trang_thai : "");
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo", nv);
        return "admin/thuoc_tinh/cong_suat";
    }

    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        CongSuat congSuat = congSuatRepo.findById(id).orElse(null);
        if (congSuat != null) {
            if (congSuat.getTrang_thai()) {
                long countTrue = congSuatRepo.findAll().stream()
                        .filter(c -> c.getTrang_thai() && !c.getId().equals(id))
                        .count();

                if (countTrue == 0) {
                    return ResponseEntity.badRequest().body("Cần có ít nhất một công suất hoạt động");
                }
            }
            congSuat.setTrang_thai(!congSuat.getTrang_thai());
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
        Integer congSuatInt;
        try {
            congSuatInt = Integer.parseInt(tenCongSuat.trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Công suất không hợp lệ.");
        }

        if (congSuatInt < 30) {
            return ResponseEntity.badRequest().body("Công suất nhỏ nhất là 30W");
        } else if (congSuatInt > 500) {
            return ResponseEntity.badRequest().body("Công suất lớn nhất là 500W");
        }

        String tenCSStr = congSuatInt + "W";

        Optional<CongSuat> checkTonTai = congSuatRepo.findByTen(tenCSStr.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại công suất.");
        } else {
            CongSuat congSuat = new CongSuat();
            congSuat.setTen(tenCSStr.trim());
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

        Integer congSuatInt;
        try {
            congSuatInt = Integer.parseInt(tenCongSuat.trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Công suất không hợp lệ.");
        }

        if (congSuatInt < 30) {
            return ResponseEntity.badRequest().body("Công suất nhỏ nhất là 30W");
        } else if (congSuatInt > 500) {
            return ResponseEntity.badRequest().body("Công suất lớn nhất là 500W");
        }

        String tenCSStr = congSuatInt + "W";
        Optional<CongSuat> checkTonTai = congSuatRepo.findByTen(tenCSStr.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại công suất.");
        }
        Optional<CongSuat> congSuatOptional = congSuatRepo.findById(id);
        if (congSuatOptional.isPresent()) {
            CongSuat congSuat = congSuatOptional.get();
            congSuat.setTen(tenCSStr);
            congSuatRepo.save(congSuat);
            return ResponseEntity.ok("Cập nhật công suất thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy công suất.");
        }
    }
}
