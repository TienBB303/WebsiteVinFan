package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.thuoc_tinh.Hang;
import com.example.datn.repository.ThuocTinhRepo.HangRepo;
import com.example.datn.repository.ThuocTinhRepo.HangRepo;
import com.example.datn.service.thuoc_tinh_service.HangService;
import com.example.datn.service.thuoc_tinh_service.HangService;
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
@RequestMapping("/admin/thuoc-tinh/hang")
public class HangController {
    @Autowired
    HangRepo ttRepo;

    @Autowired
    HangService ttService;

    @ModelAttribute("listHang")
    public List<Hang> listHang() {
        return ttRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_hang", defaultValue = "") String ten_hang,
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
        Page<Hang> searchPage = ttService.search(ten_hang, trang_thai, PageRequest.of(page, size));
        model.addAttribute("listTT", searchPage);
        model.addAttribute("ten_hang", ten_hang);
        model.addAttribute("trang_thai", trang_thaiStr);
        return "admin/thuoc_tinh/hang";
    }

    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        Hang tt = ttRepo.findById(id).orElse(null);
        if (tt != null) {
            if (tt.getTrang_thai()) {
                long countTrue = ttRepo.findAll().stream()
                        .filter(c -> c.getTrang_thai() && !c.getId().equals(id))
                        .count();

                if (countTrue == 0) {
                    return ResponseEntity.badRequest().body("Cần có ít nhất một hãng hoạt động");
                }
            }
            tt.setTrang_thai(!tt.getTrang_thai());
            ttRepo.save(tt);
            return ResponseEntity.ok("Hãng thay đổi trạng thái thành công.");
        } else {
            return ResponseEntity.badRequest().body("Không có hãng hiện tại.");
        }
    }

    @PostMapping("/addHang")
    public ResponseEntity<?> themMoi(@RequestParam("ten_hang") String tenHang) {
        if (tenHang == null || tenHang.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên hãng không được để trống.");
        }
        Optional<Hang> checkTonTai = ttRepo.findByTen(tenHang.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại hãng.");
        } else {
            Hang tt = new Hang();
            tt.setTen(tenHang.trim());
            tt.setTrang_thai(true);
            ttRepo.save(tt);
            return ResponseEntity.ok("Hãng thêm mới thành công.");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> thongTin(@PathVariable("id") Integer id) {
        Optional<Hang> tt = ttRepo.findById(id);
        if (tt.isPresent()) {
            System.out.println("Found Hang: " + tt.get());
            return ResponseEntity.ok(tt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại hãng.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateHang(@RequestParam("id") Integer id, @RequestParam("ten_hang_update") String tenHang) {
        if (tenHang == null || tenHang.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên hãng không được để trống.");
        }
        Optional<Hang> checkTonTai = ttRepo.findByTen(tenHang.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại hãng.");
        }
        Optional<Hang> ttOptional = ttRepo.findById(id);
        if (ttOptional.isPresent()) {
            Hang tt = ttOptional.get();
            tt.setTen(tenHang);
            ttRepo.save(tt);
            return ResponseEntity.ok("Cập nhật hãng thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy hãng.");
        }
    }
}
