package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.NhanVien;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.entity.thuoc_tinh.CongSuat;
import com.example.datn.entity.thuoc_tinh.KieuQuat;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.repository.ThuocTinhRepo.KieuQuatRepo;
import com.example.datn.service.thuoc_tinh_service.KieuQuatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/thuoc-tinh/kieu-quat")
public class KieuQuatController {
    @Autowired
    KieuQuatRepo kieuQuatRepo;

    @Autowired
    KieuQuatService kieuQuatService;
    @Autowired
    NhanVienRepository nhanVienRepository;
    @ModelAttribute("listKieuQuat")
    public List<KieuQuat> listKieuQuat() {
        return kieuQuatRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_kieu", defaultValue = "") String ten_kieu,
                          @RequestParam(value = "trang_thai", defaultValue = "") Boolean trang_thai,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "5") int size,
                          Model model) {
        Page<KieuQuat> searchPage = kieuQuatService.search(ten_kieu.trim(), trang_thai, PageRequest.of(page, size));
        model.addAttribute("listKQ", searchPage);
        model.addAttribute("ten_kieu", ten_kieu);
        model.addAttribute("trang_thai", trang_thai != null ? trang_thai : "");
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo", nv);
        return "admin/thuoc_tinh/kieu_quat";
    }


    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        KieuQuat kieuQuat = kieuQuatRepo.findById(id).orElse(null);
        if (kieuQuat != null) {
            if (kieuQuat.getTrang_thai()) {
                long countTrue = kieuQuatRepo.findAll().stream()
                        .filter(c -> c.getTrang_thai() && !c.getId().equals(id))
                        .count();

                if (countTrue == 0) {
                    return ResponseEntity.badRequest().body("Cần có ít nhất một kiểu quạt hoạt động");
                }
            }
            kieuQuat.setTrang_thai(!kieuQuat.getTrang_thai());
            kieuQuatRepo.save(kieuQuat);
            return ResponseEntity.ok("Kiểu quạt thay đổi trạng thái thành công.");
        } else {
            return ResponseEntity.badRequest().body("Không có kiểu quạt hiện tại.");
        }
    }

    @PostMapping("/addKieuQuat")
    public ResponseEntity<?> themMoi(@RequestParam("ten_kieu_quat") String tenKieu) {
        if (tenKieu == null || tenKieu.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên kiểu quạt không được để trống.");
        }
        Optional<KieuQuat> checkTonTai = kieuQuatRepo.findByTen(tenKieu.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại kiểu quạt.");
        } else {
            KieuQuat kieuQuat = new KieuQuat();
            kieuQuat.setTen(tenKieu.trim());
            kieuQuat.setTrang_thai(true);
            kieuQuatRepo.save(kieuQuat);
            return ResponseEntity.ok("Kiểu quạt thêm mới thành công.");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> thongTin(@PathVariable("id") Integer id) {
        Optional<KieuQuat> kieuQuat = kieuQuatRepo.findById(id);
        if (kieuQuat.isPresent()) {
            System.out.println("Found KieuQuat: " + kieuQuat.get());
            return ResponseEntity.ok(kieuQuat.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại kiểu quạt.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateKieuQuat(@RequestParam("id") Integer id, @RequestParam("ten_kieu_quat_update") String tenKieu) {
        if (tenKieu == null || tenKieu.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên kiểu quạt không được để trống.");
        }

        Optional<KieuQuat> checkTonTai = kieuQuatRepo.findByTen(tenKieu.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại kiểu quạt.");
        }
        Optional<KieuQuat> kieuQuatOptional = kieuQuatRepo.findById(id);
        if (kieuQuatOptional.isPresent()) {
            KieuQuat kieuQuat = kieuQuatOptional.get();
            kieuQuat.setTen(tenKieu);
            kieuQuatRepo.save(kieuQuat);
            return ResponseEntity.ok("Cập nhật kiểu quạt thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy kiểu quạt.");
        }
    }
}
