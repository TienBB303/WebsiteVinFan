package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.thuoc_tinh.ChieuCao;
import com.example.datn.repository.ThuocTinhRepo.ChieuCaoRepo;
import com.example.datn.service.thuoc_tinh_service.ChieuCaoService;
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
@RequestMapping("/admin/thuoc-tinh/chieu-cao")
public class ChieuCaoController {
    @Autowired
    ChieuCaoRepo chieuCaoRepo;

    @Autowired
    ChieuCaoService chieuCaoService;

    @ModelAttribute("listChieuCao")
    public List<ChieuCao> listChieuCao() {
        return chieuCaoRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_chieu_cao", defaultValue = "") String ten_chieu_cao,
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
        Page<ChieuCao> searchPage = chieuCaoService.search(ten_chieu_cao, trang_thai, PageRequest.of(page, size));
        model.addAttribute("listCC", searchPage);
        model.addAttribute("ten_chieu_cao", ten_chieu_cao);
        model.addAttribute("trang_thai", trang_thaiStr);
        return "admin/thuoc_tinh/chieu_cao";
    }

    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        ChieuCao chieuCao = chieuCaoRepo.findById(id).orElse(null);
        if (chieuCao != null) {
            if (chieuCao.getTrang_thai() == true) {
                chieuCao.setTrang_thai(false);
            } else if (chieuCao.getTrang_thai() == false) {
                chieuCao.setTrang_thai(true);
            }
            chieuCaoRepo.save(chieuCao);
            return ResponseEntity.ok("Chiều cao thay đổi trạng thái thành công.");
        } else {
            return ResponseEntity.badRequest().body("Không có chiều cao hiện tại.");
        }
    }

    @PostMapping("/addChieuCao")
    public ResponseEntity<?> themMoi(@RequestParam("ten_chieu_cao") String tenChieuCao) {
        if (tenChieuCao == null || tenChieuCao.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên chiều cao không được để trống.");
        }
        Integer chieuCaoInt;
        try {
            chieuCaoInt = Integer.parseInt(tenChieuCao.trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Chiều cao không hợp lệ.");
        }

        if (chieuCaoInt < 30) {
            return ResponseEntity.badRequest().body("Chiều cao không nhỏ hơn 30 cm.");
        } else if (chieuCaoInt > 300) {
            return ResponseEntity.badRequest().body("Chiều cao không vượt quá 300 cm.");
        }

        String chieuCaoStr = chieuCaoInt + " cm";

        Optional<ChieuCao> checkTonTai = chieuCaoRepo.findByTen(chieuCaoStr.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại chiều cao.");
        } else {
            ChieuCao chieuCao = new ChieuCao();
            chieuCao.setTen(chieuCaoStr.trim());
            chieuCao.setTrang_thai(true);
            chieuCaoRepo.save(chieuCao);
            return ResponseEntity.ok("Chiều cao thêm mới thành công.");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> thongTin(@PathVariable("id") Integer id) {
        Optional<ChieuCao> chieuCao = chieuCaoRepo.findById(id);
        if (chieuCao.isPresent()) {
            System.out.println("Found ChieuCao: " + chieuCao.get());
            return ResponseEntity.ok(chieuCao.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại chiều cao.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateChieuCao(@RequestParam("id") Integer id, @RequestParam("ten_chieu_cao_update") String tenChieuCao) {
        if (tenChieuCao == null || tenChieuCao.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên chiều cao không được để trống.");
        }

        Integer chieuCaoInt;
        try {
            chieuCaoInt = Integer.parseInt(tenChieuCao.trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Chiều cao không hợp lệ.");
        }

        if (chieuCaoInt < 30) {
            return ResponseEntity.badRequest().body("Chiều cao không nhỏ hơn 30 cm.");
        } else if (chieuCaoInt > 300) {
            return ResponseEntity.badRequest().body("Chiều cao không vượt quá 300 cm.");
        }

        String chieuCaoStr = chieuCaoInt + " cm";

        Optional<ChieuCao> checkTonTai = chieuCaoRepo.findByTen(chieuCaoStr.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại chiều cao.");
        }
        Optional<ChieuCao> chieuCaoOptional = chieuCaoRepo.findById(id);
        if (chieuCaoOptional.isPresent()) {
            ChieuCao chieuCao = chieuCaoOptional.get();
            chieuCao.setTen(chieuCaoStr);
            chieuCaoRepo.save(chieuCao);
            return ResponseEntity.ok("Cập nhật chiều cao thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chiều cao.");
        }
    }
}
