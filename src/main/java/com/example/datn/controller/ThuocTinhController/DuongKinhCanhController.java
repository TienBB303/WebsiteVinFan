package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.thuoc_tinh.DuongKinhCanh;
import com.example.datn.entity.thuoc_tinh.DuongKinhCanh;
import com.example.datn.repository.ThuocTinhRepo.DuongKinhCanhRepo;
import com.example.datn.repository.ThuocTinhRepo.DuongKinhCanhRepo;
import com.example.datn.service.thuoc_tinh_service.DuongKinhCanhService;
import com.example.datn.service.thuoc_tinh_service.DuongKinhCanhService;
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
@RequestMapping("/admin/thuoc-tinh/duong-kinh-canh")
public class DuongKinhCanhController {
    @Autowired
    DuongKinhCanhRepo ttRepo;

    @Autowired
    DuongKinhCanhService ttService;

    @ModelAttribute("listDuongKinhCanh")
    public List<DuongKinhCanh> listDuongKinhCanh() {
        return ttRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_duong_kinh_canh", defaultValue = "") String ten_duong_kinh_canh,
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
        Page<DuongKinhCanh> searchPage = ttService.search(ten_duong_kinh_canh, trang_thai, PageRequest.of(page, size));
        model.addAttribute("listTT", searchPage);
        model.addAttribute("ten_duong_kinh_canh", ten_duong_kinh_canh);
        model.addAttribute("trang_thai", trang_thaiStr);
        return "admin/thuoc_tinh/duong_kinh_canh";
    }

    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        DuongKinhCanh tt = ttRepo.findById(id).orElse(null);
        if (tt != null) {
            if (tt.getTrang_thai()) {
                long countTrue = ttRepo.findAll().stream()
                        .filter(c -> c.getTrang_thai() && !c.getId().equals(id))
                        .count();

                if (countTrue == 0) {
                    return ResponseEntity.badRequest().body("Cần có ít nhất một đường kính cánh hoạt động");
                }
            }
            tt.setTrang_thai(!tt.getTrang_thai());
            ttRepo.save(tt);
            return ResponseEntity.ok("Đường kính cánh thay đổi trạng thái thành công.");
        } else {
            return ResponseEntity.badRequest().body("Không có đường kính cánh hiện tại.");
        }
    }

    @PostMapping("/addDuongKinhCanh")
    public ResponseEntity<?> themMoi(@RequestParam("ten_duong_kinh_canh") String tenDuongKinhCanh) {
        if (tenDuongKinhCanh == null || tenDuongKinhCanh.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Đường kính cánh không được để trống.");
        }

        Integer duongKinh;
        try {
            duongKinh = Integer.parseInt(tenDuongKinhCanh.trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Đường kính cánh không hợp lệ.");
        }

        if (duongKinh < 30) {
            return ResponseEntity.badRequest().body("Đường kính cánh không nhỏ hơn 30 cm.");
        } else if (duongKinh > 300) {
            return ResponseEntity.badRequest().body("Đường kính cánh không vượt quá 300 cm.");
        }

        String tenDKCStr = duongKinh + " cm";

        Optional<DuongKinhCanh> checkTonTai = ttRepo.findByTen(tenDKCStr.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại đường kính cánh.");
        } else {
            DuongKinhCanh tt = new DuongKinhCanh();
            tt.setTen(tenDKCStr.trim());
            tt.setTrang_thai(true);
            ttRepo.save(tt);
            return ResponseEntity.ok("Đường kính cánh thêm mới thành công.");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> thongTin(@PathVariable("id") Integer id) {
        Optional<DuongKinhCanh> tt = ttRepo.findById(id);
        if (tt.isPresent()) {
            System.out.println("Found DuongKinhCanh: " + tt.get());
            return ResponseEntity.ok(tt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại đường kính cánh.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateDuongKinhCanh(@RequestParam("id") Integer id, @RequestParam("ten_duong_kinh_canh_update") String tenDuongKinhCanh) {
        if (tenDuongKinhCanh == null || tenDuongKinhCanh.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên đường kính cánh không được để trống.");
        }
        Integer duongKinh;
        try {
            duongKinh = Integer.parseInt(tenDuongKinhCanh.trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Đường kính cánh không hợp lệ.");
        }

        if (duongKinh < 30) {
            return ResponseEntity.badRequest().body("Đường kính cánh không nhỏ hơn 30 cm.");
        } else if (duongKinh > 300) {
            return ResponseEntity.badRequest().body("Đường kính cánh không vượt quá 300 cm.");
        }

        String tenDKCStr = duongKinh + " cm";

        Optional<DuongKinhCanh> checkTonTai = ttRepo.findByTen(tenDKCStr.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại đường kính cánh.");
        }
        Optional<DuongKinhCanh> ttOptional = ttRepo.findById(id);
        if (ttOptional.isPresent()) {
            DuongKinhCanh tt = ttOptional.get();
            tt.setTen(tenDKCStr);
            ttRepo.save(tt);
            return ResponseEntity.ok("Cập nhật đường kính cánh thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đường kính cánh.");
        }
    }
}
