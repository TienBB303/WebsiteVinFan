package com.example.datn.controller;

import com.example.datn.entity.DTO.SanPhamDTO;
import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.entity.SanPhamChiTietTam;
import com.example.datn.entity.SanPhamTam;
import com.example.datn.entity.thuoc_tinh.*;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.repository.SanPhamRepo;
import com.example.datn.repository.ThuocTinhRepo.*;
import com.example.datn.service.SanPhamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class SanPhamController_backup {
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
    public String searchProducts(@RequestParam(value = "query", defaultValue = "") String query,
                                 @RequestParam(value = "minPrice", defaultValue = "0") BigDecimal minPrice,
                                 @RequestParam(value = "maxPrice", defaultValue = "0") BigDecimal maxPrice,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        if (maxPrice.compareTo(BigDecimal.ZERO) == 0) {
            maxPrice = new BigDecimal("9999999999");
        }
        Page<SanPhamChiTiet> searchPage = sanPhamService.searchProducts(query, minPrice, maxPrice, PageRequest.of(page, size));
        model.addAttribute("listSP", searchPage);
        model.addAttribute("query", query);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        return "admin/san_pham/san_pham_index";
    }

    @GetMapping("/san-pham/viewUpdate/{id}")
    public String viewUpdateProduct(@PathVariable("id") Long id, Model model) {
        SanPhamChiTiet sanPhamChiTiet = sanPhamService.findById(id);
        if (sanPhamChiTiet == null) {
            return "redirect:/admin/san-pham";
        }
        model.addAttribute("spUpdate", sanPhamChiTiet);
        model.addAttribute("hinhAnhHienTai", sanPhamChiTiet.getHinh_anh());
        return "admin/san_pham/san_pham_update";
    }


    @GetMapping("/san-pham/viewAdd")
    public String viewAddProduct() {
        return "admin/san_pham/san_pham_add";
    }

    @PostMapping("/san-pham/add-bien-the")
    public String addProduct(
            @RequestParam("sanPham.ten") String ten,
            @RequestParam("sanPham.kieuQuat.id") Integer kieuQuatId,
            @RequestParam("mauSac.id") List<Integer> mauSacIds,
            @RequestParam("cheDoGio.id") List<Integer> cheDoGioIds,
            @RequestParam("congSuat.id") List<Integer> congSuatIds,
            @RequestParam("nutBam.id") Integer nutBamId,
            @RequestParam("chatLieuCanh.id") Integer chatLieuCanhId,
            @RequestParam("duongKinhCanh.id") Integer duongKinhCanhId,
            @RequestParam("chatLieuKhung.id") Integer chatLieuKhungId,
            @RequestParam("deQuat.id") Integer deQuatId,
            @RequestParam("chieuCao.id") Integer chieuCaoId,
            @RequestParam("hang.id") Integer hangId,
            HttpSession session, Model model) {

        SanPhamTam sanPhamTam = new SanPhamTam();
        String ma = sanPhamService.taoMaTuDong();  // Auto-generate product code
        sanPhamTam.setMa(ma);
        sanPhamTam.setTen(ten);
        sanPhamTam.setKieuQuat(kieuQuatRepo.findById(kieuQuatId).orElse(null));
        sanPhamTam.setTrang_thai(true);
        sanPhamTam.setNgay_tao(new Date());

        List<SanPhamChiTietTam> listSPCTTam = new ArrayList<>();

        for (Integer mauSacId : mauSacIds) {
            MauSac mauSac = mauSacRepo.findById(mauSacId).orElse(null);
            for (Integer congSuatId : congSuatIds) {
                CongSuat congSuat = congSuatRepo.findById(congSuatId).orElse(null);
                for (Integer cheDoGioId : cheDoGioIds) {
                    CheDoGio cheDoGio = cheDoGioRepo.findById(cheDoGioId).orElse(null);

                    SanPhamChiTietTam sanPhamChiTietTam = new SanPhamChiTietTam();
                    sanPhamChiTietTam.setSanPhamTam(sanPhamTam);
                    sanPhamChiTietTam.setMauSac(mauSac);
                    sanPhamChiTietTam.setCongSuat(congSuat);
                    sanPhamChiTietTam.setCheDoGio(cheDoGio);
                    sanPhamChiTietTam.setNutBam(nutBamRepo.findById(nutBamId).orElse(null));
                    sanPhamChiTietTam.setChatLieuCanh(chatLieuCanhRepo.findById(chatLieuCanhId).orElse(null));
                    sanPhamChiTietTam.setDuongKinhCanh(duongKinhCanhRepo.findById(duongKinhCanhId).orElse(null));
                    sanPhamChiTietTam.setChatLieuKhung(chatLieuKhungRepo.findById(chatLieuKhungId).orElse(null));
                    sanPhamChiTietTam.setDeQuat(deQuatRepo.findById(deQuatId).orElse(null));
                    sanPhamChiTietTam.setChieuCao(chieuCaoRepo.findById(chieuCaoId).orElse(null));
                    sanPhamChiTietTam.setHang(hangRepo.findById(hangId).orElse(null));
                    sanPhamChiTietTam.setDieuKhienTuXa(new DieuKhienTuXa(2));

                    sanPhamChiTietTam.setGia(new BigDecimal(100000));
                    sanPhamChiTietTam.setSo_luong(1);
                    sanPhamChiTietTam.setTrang_thai(true);
                    sanPhamChiTietTam.setNgay_tao(new Date());
                    sanPhamChiTietTam.setNguoi_tao("admin");

                    listSPCTTam.add(sanPhamChiTietTam);
                }
            }
        }

        // Store the list in the session
        session.setAttribute("listSPCTTam", listSPCTTam);
        model.addAttribute("listSPCTTam", listSPCTTam);
        return "admin/san_pham/san_pham_add";
    }

    // Confirm the product variants from session
    @PostMapping("/san-pham/confirm")
    public String confirmProducts(HttpSession session, Model model) {

        // Retrieve the list of product variants from the session
        @SuppressWarnings("unchecked")
        List<SanPhamChiTietTam> sanPhamChiTietTamList = (List<SanPhamChiTietTam>) session.getAttribute("listSPCTTam");

        // Check if the list is null or empty
        if (sanPhamChiTietTamList == null || sanPhamChiTietTamList.isEmpty()) {
            model.addAttribute("error", "No product variants to confirm.");
            return "admin/san_pham/san_pham_add";
        }

        // Process and confirm the variants
        SanPham sanPham = new SanPham();
        List<SanPhamChiTiet> listSPCT = new ArrayList<>();

        for (SanPhamChiTietTam spTam : sanPhamChiTietTamList) {
            sanPham.setTen(spTam.getSanPhamTam().getTen());
            sanPham.setMa(spTam.getSanPhamTam().getMa());
            sanPham.setKieuQuat(spTam.getSanPhamTam().getKieuQuat());
            sanPham.setTrang_thai(spTam.getSanPhamTam().getTrang_thai());
            sanPham.setNgay_tao(spTam.getSanPhamTam().getNgay_tao());

            SanPhamChiTiet sanPhamChiTiet = new SanPhamChiTiet();
            sanPhamChiTiet.setSanPham(sanPham);
            sanPhamChiTiet.setMauSac(spTam.getMauSac());
            sanPhamChiTiet.setCongSuat(spTam.getCongSuat());
            sanPhamChiTiet.setCheDoGio(spTam.getCheDoGio());
            sanPhamChiTiet.setChieuCao(spTam.getChieuCao());
            sanPhamChiTiet.setDuongKinhCanh(spTam.getDuongKinhCanh());
            sanPhamChiTiet.setChatLieuKhung(spTam.getChatLieuKhung());
            sanPhamChiTiet.setNutBam(spTam.getNutBam());
            sanPhamChiTiet.setDeQuat(spTam.getDeQuat());

            sanPhamChiTiet.setGia(spTam.getGia());
            sanPhamChiTiet.setSo_luong(spTam.getSo_luong());
            sanPhamChiTiet.setTrang_thai(spTam.getTrang_thai());
            sanPhamChiTiet.setNgay_tao(spTam.getNgay_tao());
            sanPhamChiTiet.setNguoi_tao(spTam.getNguoi_tao());

            listSPCT.add(sanPhamChiTiet);
        }

        // Save the product and its variants
        sanPhamService.create(sanPham, listSPCT);

        // Clear session after confirming
        session.removeAttribute("listSPCTTam");

        return "redirect:/admin/san-pham";
    }

}
