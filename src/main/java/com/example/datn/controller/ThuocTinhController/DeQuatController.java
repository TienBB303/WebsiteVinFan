package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.NhanVien;
import com.example.datn.entity.thuoc_tinh.DeQuat;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.repository.ThuocTinhRepo.DeQuatRepo;
import com.example.datn.service.thuoc_tinh_service.DeQuatService;
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
@RequestMapping("/admin/thuoc-tinh/de-quat")
public class DeQuatController {
    @Autowired
    DeQuatRepo ttRepo;

    @Autowired
    DeQuatService ttService;

    @Autowired
    NhanVienRepository nhanVienRepository;
    @ModelAttribute("listDeQuat")
    public List<DeQuat> listDeQuat() {
        return ttRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_de_quat", defaultValue = "") String ten_de_quat,
                          @RequestParam(value = "trang_thai", defaultValue = "") Boolean trang_thai,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "5") int size,
                          Model model) {
        Page<DeQuat> searchPage = ttService.search(ten_de_quat.trim(), trang_thai, PageRequest.of(page, size));
        model.addAttribute("listTT", searchPage);
        model.addAttribute("ten_de_quat", ten_de_quat);
        model.addAttribute("trang_thai", trang_thai != null ? trang_thai : "");
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo", nv);
        return "admin/thuoc_tinh/de_quat";
    }

    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        DeQuat tt = ttRepo.findById(id).orElse(null);
        if (tt != null) {
            if (tt.getTrang_thai()) {
                long countTrue = ttRepo.findAll().stream()
                        .filter(c -> c.getTrang_thai() && !c.getId().equals(id))
                        .count();

                if (countTrue == 0) {
                    return ResponseEntity.badRequest().body("Cần có ít nhất một đế quạt hoạt động");
                }
            }
            tt.setTrang_thai(!tt.getTrang_thai());
            ttRepo.save(tt);
            return ResponseEntity.ok("Đế quạt thay đổi trạng thái thành công.");
        } else {
            return ResponseEntity.badRequest().body("Không có đế quạt hiện tại.");
        }
    }

    @PostMapping("/addDeQuat")
    public ResponseEntity<?> themMoi(@RequestParam("ten_de_quat") String tenDeQuat) {
        if (tenDeQuat == null || tenDeQuat.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên đế quạt không được để trống.");
        }
        Optional<DeQuat> checkTonTai = ttRepo.findByTen(tenDeQuat.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại đế quạt.");
        } else {
            DeQuat tt = new DeQuat();
            tt.setTen(tenDeQuat.trim());
            tt.setTrang_thai(true);
            ttRepo.save(tt);
            return ResponseEntity.ok("Đế quạt thêm mới thành công.");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> thongTin(@PathVariable("id") Integer id) {
        Optional<DeQuat> tt = ttRepo.findById(id);
        if (tt.isPresent()) {
            System.out.println("Found DeQuat: " + tt.get());
            return ResponseEntity.ok(tt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại đế quạt.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateDeQuat(@RequestParam("id") Integer id, @RequestParam("ten_de_quat_update") String tenDeQuat) {
        if (tenDeQuat == null || tenDeQuat.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên đế quạt không được để trống.");
        }
        Optional<DeQuat> checkTonTai = ttRepo.findByTen(tenDeQuat.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại đế quạt.");
        }
        Optional<DeQuat> ttOptional = ttRepo.findById(id);
        if (ttOptional.isPresent()) {
            DeQuat tt = ttOptional.get();
            tt.setTen(tenDeQuat);
            ttRepo.save(tt);
            return ResponseEntity.ok("Cập nhật đế quạt thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đế quạt.");
        }
    }
}
