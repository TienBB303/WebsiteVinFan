package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.thuoc_tinh.CongSuat;
import com.example.datn.entity.thuoc_tinh.MauSac;
import com.example.datn.repository.ThuocTinhRepo.MauSacRepo;
import com.example.datn.repository.ThuocTinhRepo.MauSacRepo;
import com.example.datn.service.thuoc_tinh_service.MauSacService;
import com.example.datn.service.thuoc_tinh_service.MauSacService;
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
@RequestMapping("/admin/thuoc-tinh/mau-sac")
public class MauSacController {
    @Autowired
    MauSacRepo mauSacRepo;

    @Autowired
    MauSacService mauSacService;

    @ModelAttribute("listMauSac")
    public List<MauSac> listMauSac() {
        return mauSacRepo.findAll();
    }

    @GetMapping("/view")
    public String timKiem(@RequestParam(value = "ten_mau_sac", defaultValue = "") String ten_mau_sac,
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
        Page<MauSac> searchPage = mauSacService.search(ten_mau_sac, trang_thai, PageRequest.of(page, size));
        model.addAttribute("listMS", searchPage);
        model.addAttribute("ten_mau_sac", ten_mau_sac);
        model.addAttribute("trang_thai", trang_thaiStr);
        return "admin/thuoc_tinh/mau_sac";
    }

    @PostMapping("/doi-trang-thai/{id}")
    public ResponseEntity doiTrangThai(@PathVariable("id") Integer id) {
        MauSac mauSac = mauSacRepo.findById(id).orElse(null);
        if (mauSac != null) {
            if (mauSac.getTrang_thai() == true) {
                mauSac.setTrang_thai(false);
            } else if (mauSac.getTrang_thai() == false) {
                mauSac.setTrang_thai(true);
            }
            mauSacRepo.save(mauSac);
            return ResponseEntity.ok("Màu sắc thay đổi trạng thái thành công.");
        } else {
            return ResponseEntity.badRequest().body("Không có màu sắc hiện tại.");
        }
    }

    @PostMapping("/addMauSac")
    public ResponseEntity<?> themMoi(@RequestParam("ten_mau_sac") String tenMauSac) {
        if (tenMauSac == null || tenMauSac.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên màu sắc không được để trống.");
        }
        Optional<MauSac> checkTonTai = mauSacRepo.findByTen(tenMauSac.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại màu sắc.");
        } else {
            MauSac mauSac = new MauSac();
            mauSac.setTen(tenMauSac.trim());
            mauSac.setTrang_thai(true);
            mauSacRepo.save(mauSac);
            return ResponseEntity.ok("Màu sắc thêm mới thành công.");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> thongTin(@PathVariable("id") Integer id) {
        Optional<MauSac> mauSac = mauSacRepo.findById(id);
        if (mauSac.isPresent()) {
            System.out.println("Found MauSac: " + mauSac.get());
            return ResponseEntity.ok(mauSac.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại màu sắc.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateMauSac(@RequestParam("id") Integer id, @RequestParam("ten_mau_sac_update") String tenMauSac) {
        if (tenMauSac == null || tenMauSac.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên màu sắc không được để trống.");
        }

        Optional<MauSac> checkTonTai = mauSacRepo.findByTen(tenMauSac.trim());
        if (checkTonTai.isPresent()) {
            return ResponseEntity.badRequest().body("Đã tồn tại màu sắc.");
        }
        Optional<MauSac> mauSacOptional = mauSacRepo.findById(id);
        if (mauSacOptional.isPresent()) {
            MauSac mauSac = mauSacOptional.get();
            mauSac.setTen(tenMauSac);
            mauSacRepo.save(mauSac);
            return ResponseEntity.ok("Cập nhật màu sắc thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy màu sắc.");
        }
    }
}
