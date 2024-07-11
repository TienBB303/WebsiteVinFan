package com.example.vinfan.controller;

import com.example.vinfan.entity.SanPhamChiTiet;
import com.example.vinfan.entity.SanPham;
import com.example.vinfan.entity.thuoc_tinh.*;
import com.example.vinfan.repository.*;
import com.example.vinfan.repository.ThuocTinhRepo.*;
import com.example.vinfan.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class SanPhamController {
    @Autowired
    SanPhamService sanPhamService;

    @Autowired
    ChatLieuCanhRepo chatLieuCanhRepo;
    @Autowired
    ChatLieuKhungRepo chatLieuKhungRepo;
    @Autowired
    CheDoGioRepo cheDoGioRepo;
    @Autowired
    ChieuCaoRepo chieuCaoRepo;
    @Autowired
    CongSuatRepo congSuatRepo;
    @Autowired
    DeQuatRepo deQuatRepo;
    @Autowired
    DieuKhienTuXaRepo dieuKhienTuXaRepo;
    @Autowired
    DuongKinhCanhRepo duongKinhCanhRepo;
    @Autowired
    HangRepo hangRepo;
    @Autowired
    KieuQuatRepo kieuQuatRepo;
    @Autowired
    MauSacRepo mauSacRepo;
    @Autowired
    NutBamRepo nutBamRepo;
    @Autowired
    SPCTRepo spctRepo;
    @Autowired
    SanPhamRepo sanPhamRepo;

    @ModelAttribute("listMau")
    public List<MauSac> listMau() {
        return mauSacRepo.findAll();
    }

    @ModelAttribute("listChatLieuCanh")
    public List<ChatLieuCanh> listChatLieuCanh() {
        return chatLieuCanhRepo.findAll();
    }

    @ModelAttribute("listKieuQuat")
    public List<KieuQuat> listKieuQuat() {
        return kieuQuatRepo.findAll();
    }

    @ModelAttribute("listCongSuat")
    public List<CongSuat> listCongSuat() {
        return congSuatRepo.findAll();
    }

    @ModelAttribute("listDeQuat")
    public List<DeQuat> listDeQuat() {
        return deQuatRepo.findAll();
    }

    @ModelAttribute("listHang")
    public List<Hang> listHang() {
        return hangRepo.findAll();
    }

    @ModelAttribute("listCheDoGio")
    public List<CheDoGio> listCheDoGio() {
        return cheDoGioRepo.findAll();
    }

    @ModelAttribute("listDieuKhienTuXa")
    public List<DieuKhienTuXa> listDieuKhienTuXa() {
        return dieuKhienTuXaRepo.findAll();
    }

    @ModelAttribute("listDuongKinhCanh")
    public List<DuongKinhCanh> listDuongKinhCanh() {
        return duongKinhCanhRepo.findAll();
    }

    @ModelAttribute("listNutBam")
    public List<NutBam> listNutBam() {
        return nutBamRepo.findAll();
    }

    @ModelAttribute("listChieuCao")
    public List<ChieuCao> listChieuCao() {
        return chieuCaoRepo.findAll();
    }

    @ModelAttribute("listChatLieuKhung")
    public List<ChatLieuKhung> listChatLieuKhung() {
        return chatLieuKhungRepo.findAll();
    }

    @GetMapping("/san-pham")
    public String sanPhamChiTietHienThi(Model model) {
        List<SanPhamChiTiet> listSP = sanPhamService.getAll();
        model.addAttribute("listSP", listSP);
        return "/admin/san_pham/index";
    }

    @PostMapping("/san-pham/add")
    public String themSanPham(@ModelAttribute SanPhamChiTiet sanPhamChiTiet,
                              @RequestParam("hinhAnhFile") MultipartFile hinhAnhFile) {
        try {
            if (!hinhAnhFile.isEmpty()) {
                String fileName = hinhAnhFile.getOriginalFilename();
                // Lưu tên file vào thuộc tính hinh_anh của sanPhamChiTiet
                sanPhamChiTiet.setHinh_anh(fileName);
                // Lưu file vào thư mục uploads (hoặc cơ sở dữ liệu)
                File file = new File("/static/admin/images/" + fileName);
                hinhAnhFile.transferTo(file);
            }

            // Thiết lập các thông tin mặc định
            sanPhamChiTiet.setNgay_tao(new Date());
            sanPhamChiTiet.setNguoi_tao("Admin"); // hoặc lấy từ người dùng đăng nhập

            sanPhamService.create(sanPhamChiTiet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/san-pham";
    }

}
