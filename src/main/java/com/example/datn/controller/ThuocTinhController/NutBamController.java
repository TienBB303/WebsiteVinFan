package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.thuoc_tinh.NutBam;
import com.example.datn.repository.ThuocTinhRepo.NutBamRepo;
import com.example.datn.repository.ThuocTinhRepo.NutBamRepo;
import com.example.datn.repository.ThuocTinhRepo.NutBamRepo;
import com.example.datn.service.thuoc_tinh_service.NutBamService;
import com.example.datn.service.thuoc_tinh_service.NutBamService;
import com.example.datn.service.thuoc_tinh_service.NutBamService;
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
@RequestMapping("/admin/thuoc-tinh/nut-bam")
public class NutBamController {
    @Autowired
    NutBamRepo ttRepo;

    @Autowired
    NutBamService ttService;

    @ModelAttribute("listNutBam")
    public List<NutBam> listNutBam() {
        return ttRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_nut_bam", defaultValue = "") String ten_nut_bam,
                          @RequestParam(value = "trang_thai", defaultValue = "") Boolean trang_thai,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "5") int size,
                          Model model) {
        Page<NutBam> searchPage = ttService.search(ten_nut_bam.trim(), trang_thai, PageRequest.of(page, size));
        model.addAttribute("listTT", searchPage);
        model.addAttribute("ten_nut_bam", ten_nut_bam);
        model.addAttribute("trang_thai", trang_thai != null ? trang_thai : "");
        return "admin/thuoc_tinh/nut_bam";
    }

    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        NutBam tt = ttRepo.findById(id).orElse(null);
        if (tt != null) {
            if (tt.getTrang_thai()) {
                long countTrue = ttRepo.findAll().stream()
                        .filter(c -> c.getTrang_thai() && !c.getId().equals(id))
                        .count();

                if (countTrue == 0) {
                    return ResponseEntity.badRequest().body("Cần có ít nhất một nút bấm hoạt động");
                }
            }
            tt.setTrang_thai(!tt.getTrang_thai());
            ttRepo.save(tt);
            return ResponseEntity.ok("Nút bấm thay đổi trạng thái thành công.");
        } else {
            return ResponseEntity.badRequest().body("Không có nút bấm hiện tại.");
        }
    }

    @PostMapping("/addNutBam")
    public ResponseEntity<?> themMoi(@RequestParam("ten_nut_bam") String tenNutBam) {
        if (tenNutBam == null || tenNutBam.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên nút bấm không được để trống.");
        }
        Optional<NutBam> checkTonTai = ttRepo.findByTen(tenNutBam.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại nút bấm.");
        } else {
            NutBam tt = new NutBam();
            tt.setTen(tenNutBam.trim());
            tt.setTrang_thai(true);
            ttRepo.save(tt);
            return ResponseEntity.ok("Nút bấm thêm mới thành công.");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> thongTin(@PathVariable("id") Integer id) {
        Optional<NutBam> tt = ttRepo.findById(id);
        if (tt.isPresent()) {
            System.out.println("Found NutBam: " + tt.get());
            return ResponseEntity.ok(tt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại nút bấm.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateNutBam(@RequestParam("id") Integer id, @RequestParam("ten_nut_bam_update") String tenNutBam) {
        if (tenNutBam == null || tenNutBam.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên nút bấm không được để trống.");
        }
        Optional<NutBam> checkTonTai = ttRepo.findByTen(tenNutBam.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại nút bấm.");
        }
        Optional<NutBam> ttOptional = ttRepo.findById(id);
        if (ttOptional.isPresent()) {
            NutBam tt = ttOptional.get();
            tt.setTen(tenNutBam);
            ttRepo.save(tt);
            return ResponseEntity.ok("Cập nhật nút bấm thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy nút bấm.");
        }
    }
}
