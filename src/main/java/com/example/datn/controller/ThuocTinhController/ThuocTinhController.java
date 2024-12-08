package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.thuoc_tinh.*;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.repository.SanPhamRepo;
import com.example.datn.repository.ThuocTinhRepo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class ThuocTinhController {

//    @Autowired
//    ChatLieuCanhRepo chatLieuCanhRepo;
//    @Autowired
//    ChatLieuKhungRepo chatLieuKhungRepo;
//    @Autowired
//    CheDoGioRepo cheDoGioRepo;
//    @Autowired
//    ChieuCaoRepo chieuCaoRepo;
//    @Autowired
//    CongSuatRepo congSuatRepo;
//    @Autowired
//    DeQuatRepo deQuatRepo;
//    @Autowired
//    DieuKhienTuXaRepo dieuKhienTuXaRepo;
//    @Autowired
//    DuongKinhCanhRepo duongKinhCanhRepo;
//    @Autowired
//    HangRepo hangRepo;
//    @Autowired
//    KieuQuatRepo kieuQuatRepo;
//    @Autowired
//    MauSacRepo mauSacRepo;
//    @Autowired
//    NutBamRepo nutBamRepo;
//
//    @ModelAttribute("listMau")
//    public List<MauSac> listMau() {
//        return mauSacRepo.findAll();
//    }
//
//    @ModelAttribute("listChatLieuCanh")
//    public List<ChatLieuCanh> listChatLieuCanh() {
//        return chatLieuCanhRepo.findAll();
//    }
//
//    @ModelAttribute("listKieuQuat")
//    public List<KieuQuat> listKieuQuat() {
//        return kieuQuatRepo.findAll();
//    }
//
//    @ModelAttribute("listCongSuat")
//    public List<CongSuat> listCongSuat() {
//        return congSuatRepo.findAll();
//    }
//
//    @ModelAttribute("listDeQuat")
//    public List<DeQuat> listDeQuat() {
//        return deQuatRepo.findAll();
//    }
//
//    @ModelAttribute("listHang")
//    public List<Hang> listHang() {
//        return hangRepo.findAll();
//    }
//
//    @ModelAttribute("listCheDoGio")
//    public List<CheDoGio> listCheDoGio() {
//        return cheDoGioRepo.findAll();
//    }
//
//    @ModelAttribute("listDieuKhienTuXa")
//    public List<DieuKhienTuXa> listDieuKhienTuXa() {
//        return dieuKhienTuXaRepo.findAll();
//    }
//
//    @ModelAttribute("listDuongKinhCanh")
//    public List<DuongKinhCanh> listDuongKinhCanh() {
//        return duongKinhCanhRepo.findAll();
//    }
//
//    @ModelAttribute("listNutBam")
//    public List<NutBam> listNutBam() {
//        return nutBamRepo.findAll();
//    }
//
//    @ModelAttribute("listChieuCao")
//    public List<ChieuCao> listChieuCao() {
//        return chieuCaoRepo.findAll();
//    }

    @GetMapping("/thuoc-tinh")
    public String hienThiThuocTinh(){
        return "admin/thuoc_tinh/thuoc_tinh";
    }

//    @GetMapping("/thuoc-tinh/addKieuQuat")
//    public String moModelAdd(@RequestParam("ten_kieu_quat") String ten_kieu_quat){
//        KieuQuat kieuQuat = new KieuQuat();
//        kieuQuat.setTen(ten_kieu_quat);
//        kieuQuatRepo.save(kieuQuat);
//        return "redirect:/admin/thuoc-tinh";
//    }
//    @PostMapping("/thuoc-tinh/cap-nhat-kieu-quat")
//    public String capNhatKieuQuat(@RequestBody List<KieuQuat> kieuQuatList) {
//        kieuQuatRepo.saveAll(kieuQuatList);
//        return "redirect:/admin/thuoc-tinh";
//    }
//
//    @DeleteMapping("/thuoc-tinh/xoa-kieu-quat/{id}")
//    public ResponseEntity<?> xoaKieuQuat(@PathVariable Integer id) {
//        kieuQuatRepo.deleteById(id);
//        return ResponseEntity.ok().build();
//    }
}
