package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.thuoc_tinh.CheDoGio;
import com.example.datn.repository.ThuocTinhRepo.CheDoGioRepo;
import com.example.datn.service.thuoc_tinh_service.CheDoGioService;
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
@RequestMapping("/admin/thuoc-tinh/che-do-gio")
public class CheDoGioController {
    @Autowired
    CheDoGioRepo cheDoGioRepo;

    @Autowired
    CheDoGioService cheDoGioService;

    @ModelAttribute("listCheDoGio")
    public List<CheDoGio> listCheDoGio() {
        return cheDoGioRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_che_do_gio", defaultValue = "") String ten_che_do_gio,
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
        Page<CheDoGio> searchPage = cheDoGioService.search(ten_che_do_gio.trim(), trang_thai, PageRequest.of(page, size));
        model.addAttribute("listCDG", searchPage);
        model.addAttribute("ten_che_do_gio", ten_che_do_gio);
        model.addAttribute("trang_thai", trang_thaiStr);
        return "admin/thuoc_tinh/che_do_gio";
    }

    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        CheDoGio cheDoGio = cheDoGioRepo.findById(id).orElse(null);
        if (cheDoGio != null) {
            if (cheDoGio.getTrang_thai()) {
                long countTrue = cheDoGioRepo.findAll().stream()
                        .filter(c -> c.getTrang_thai() && !c.getId().equals(id))
                        .count();

                if (countTrue == 0) {
                    return ResponseEntity.badRequest().body("Cần có ít nhất một chế độ gió hoạt động");
                }
            }
            cheDoGio.setTrang_thai(!cheDoGio.getTrang_thai());
            cheDoGioRepo.save(cheDoGio);
            return ResponseEntity.ok("Chế độ gió thay đổi trạng thái thành công.");
        } else {
            return ResponseEntity.badRequest().body("Không có chế độ gió hiện tại.");
        }
    }

    @PostMapping("/addCheDoGio")
    public ResponseEntity<?> themMoi(@RequestParam("ten_che_do_gio") String tenCheDoGio) {
        if (tenCheDoGio == null || tenCheDoGio.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên chế độ gió không được để trống.");
        }
        Integer cheDoGioInt;
        try {
            cheDoGioInt = Integer.parseInt(tenCheDoGio.trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Chế độ gió không hợp lệ.");
        }

        if (cheDoGioInt < 0) {
            return ResponseEntity.badRequest().body("Chế độ gió không nhỏ hơn 0");
        } else if (cheDoGioInt > 20) {
            return ResponseEntity.badRequest().body("Lớn nhất là 20 chế độ");
        }

        String tenCDGStr = cheDoGioInt + " chế độ";
        Optional<CheDoGio> checkTonTai = cheDoGioRepo.findByTen(tenCDGStr.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại chế độ gió.");
        } else {
            CheDoGio cheDoGio = new CheDoGio();
            cheDoGio.setTen(tenCDGStr.trim());
            cheDoGio.setTrang_thai(true);
            cheDoGioRepo.save(cheDoGio);
            return ResponseEntity.ok("Chế độ gió thêm mới thành công.");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> thongTin(@PathVariable("id") Integer id) {
        Optional<CheDoGio> cheDoGio = cheDoGioRepo.findById(id);
        if (cheDoGio.isPresent()) {
            System.out.println("Found CheDoGio: " + cheDoGio.get());
            return ResponseEntity.ok(cheDoGio.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại chế độ gió.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCheDoGio(@RequestParam("id") Integer id, @RequestParam("ten_che_do_gio_update") String tenCheDoGio) {
        if (tenCheDoGio == null || tenCheDoGio.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên chế độ gió không được để trống.");
        }
        Integer cheDoGioInt;
        try {
            cheDoGioInt = Integer.parseInt(tenCheDoGio.trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Chế độ gió không hợp lệ.");
        }

        if (cheDoGioInt < 0) {
            return ResponseEntity.badRequest().body("Chế độ gió không nhỏ hơn 0");
        } else if (cheDoGioInt > 20) {
            return ResponseEntity.badRequest().body("Lớn nhất là 20 chế độ");
        }

        String tenCDGStr = cheDoGioInt + " chế độ";
        Optional<CheDoGio> checkTonTai = cheDoGioRepo.findByTen(tenCDGStr.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại chế độ gió.");
        }
        Optional<CheDoGio> cheDoGioOptional = cheDoGioRepo.findById(id);
        if (cheDoGioOptional.isPresent()) {
            CheDoGio cheDoGio = cheDoGioOptional.get();
            cheDoGio.setTen(tenCDGStr);
            cheDoGioRepo.save(cheDoGio);
            return ResponseEntity.ok("Cập nhật chế độ gió thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chế độ gió.");
        }
    }
}
