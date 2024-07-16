package com.example.vinfan.controller;

import com.example.vinfan.dto.SanPhamDTO;
import com.example.vinfan.entity.SanPham;
import com.example.vinfan.entity.SanPhamChiTiet;
import com.example.vinfan.entity.thuoc_tinh.*;
import com.example.vinfan.repository.SPCTRepo;
import com.example.vinfan.repository.SanPhamRepo;
import com.example.vinfan.repository.ThuocTinhRepo.*;
import com.example.vinfan.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String trangChu(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "5") int size,
                           Model model) {
        Page<SanPhamChiTiet> sanPham = sanPhamService.findAll(PageRequest.of(page, size));
        model.addAttribute("listSP", sanPham);
        return "admin/san_pham/index";
    }

    //    //    Tìm kiếm sản phẩm :
    @GetMapping("/san-pham/search")
    public String searchProducts(@RequestParam("query") String query,
                                 @RequestParam(value = "minPrice", defaultValue = "0") BigDecimal minPrice,
                                 @RequestParam(value = "maxPrice", defaultValue = "999999") BigDecimal maxPrice,
                                 @RequestParam(value = "trangThai", defaultValue = "true") Boolean trangThai,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        Page<SanPhamChiTiet> searchPage = sanPhamService.searchProducts(query, minPrice, maxPrice, trangThai, PageRequest.of(page, size));
        model.addAttribute("listSP", searchPage);
        return "admin/san_pham/index";
    }

    @GetMapping("/san-pham/viewAdd")
    public String trangChu(Model model) {
        return "/admin/san_pham/SanPhamAdd";
    }

    @PostMapping("/san-pham/add")
    public String addProduct(
            @RequestParam("sanPham.ten") String ten,
            @RequestParam("sanPham.mo_ta") String moTa,
            @RequestParam("gia") BigDecimal gia,
            @RequestParam("gia_nhap") BigDecimal giaNhap,
            @RequestParam("so_luong") Integer soLuong,
            @RequestParam("trang_thai") Boolean trangThai,
            @RequestParam("sanPham.kieuQuat.id") Integer kieuQuatId,
            @RequestParam("mauSac.id") Integer mauSacId,
            @RequestParam("nutBam.id") Integer nutBamId,
            @RequestParam("congSuat.id") Integer congSuatId,
            @RequestParam("chatLieuCanh.id") Integer chatLieuCanhId,
            @RequestParam("duongKinhCanh.id") Integer duongKinhCanhId,
            @RequestParam("chatLieuKhung.id") Integer chatLieuKhungId,
            @RequestParam("deQuat.id") Integer deQuatId,
            @RequestParam("chieuCao.id") Integer chieuCaoId,
            @RequestParam("hang.id") Integer hangId,
            @RequestParam("cheDoGio.id") Integer cheDoGioId,
            @RequestParam("dieuKhienTuXa.id") Integer dieuKhienTuXaId,
            @RequestParam("hinhAnhFile") MultipartFile hinhAnhFile) {

        SanPham sanPham = new SanPham();
        SanPhamChiTiet sanPhamChiTiet = new SanPhamChiTiet();

        String ma = sanPhamService.taoMaTuDong();  // Tạo mã sản phẩm bằng tự động

        sanPham.setMa(ma);
        sanPham.setTen(ten);
        sanPham.setMo_ta(moTa);
        sanPham.setTrang_thai(trangThai);
        sanPham.setKieuQuat(new KieuQuat(kieuQuatId));

        sanPhamChiTiet.setGia(gia);
        sanPhamChiTiet.setGia_nhap(giaNhap);
        sanPhamChiTiet.setSo_luong(soLuong);
        sanPhamChiTiet.setSanPham(sanPham);
        sanPhamChiTiet.setMauSac(new MauSac(mauSacId));
        sanPhamChiTiet.setNutBam(new NutBam(nutBamId));
        sanPhamChiTiet.setCongSuat(new CongSuat(congSuatId));
        sanPhamChiTiet.setChatLieuCanh(new ChatLieuCanh(chatLieuCanhId));
        sanPhamChiTiet.setDuongKinhCanh(new DuongKinhCanh(duongKinhCanhId));
        sanPhamChiTiet.setChatLieuKhung(new ChatLieuKhung(chatLieuKhungId));
        sanPhamChiTiet.setDeQuat(new DeQuat(deQuatId));
        sanPhamChiTiet.setChieuCao(new ChieuCao(chieuCaoId));
        sanPhamChiTiet.setHang(new Hang(hangId));
        sanPhamChiTiet.setCheDoGio(new CheDoGio(cheDoGioId));
        sanPhamChiTiet.setDieuKhienTuXa(new DieuKhienTuXa(dieuKhienTuXaId));

        try {
            if (!hinhAnhFile.isEmpty()) {
                String fileName = hinhAnhFile.getOriginalFilename();
                sanPhamChiTiet.setHinh_anh(fileName);

                File file = new File("static/admin/images/" + fileName);
                hinhAnhFile.transferTo(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sanPham.setNgay_tao(new Date());
        sanPhamChiTiet.setNgay_tao(new Date());
        sanPhamChiTiet.setNguoi_tao("admin");

        sanPhamService.create(sanPham, sanPhamChiTiet);

        return "redirect:/admin/san-pham";
    }

    @PostMapping("/san-pham/updatePrice")
    public String updatePrice(@RequestParam("id") Long sanPhamId,
                              @RequestParam("newPrice") BigDecimal newPrice,
                              RedirectAttributes redirectAttributes) {
        try {
            SanPhamChiTiet sanPhamChiTiet = spctRepo.findById(sanPhamId).orElseThrow();
            sanPhamChiTiet.setGia(newPrice);
            spctRepo.save(sanPhamChiTiet);
            redirectAttributes.addFlashAttribute("message", "Cập nhật giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật giá thất bại!");
        }
        return "redirect:/admin/san-pham";
    }

    @PostMapping("/san-pham/update")
    public String updateProduct(
            @RequestParam("id") Long sanPhamId,
            @RequestParam("sanPham.ten") String ten,
            @RequestParam("sanPham.mo_ta") String moTa,
            @RequestParam("gia") BigDecimal gia,
            @RequestParam("gia_nhap") BigDecimal giaNhap,
            @RequestParam("so_luong") Integer soLuong,
            @RequestParam("trang_thai") Boolean trangThai,
            @RequestParam("sanPham.kieuQuat.id") Integer kieuQuatId,
            @RequestParam("mauSac.id") Integer mauSacId,
            @RequestParam("nutBam.id") Integer nutBamId,
            @RequestParam("congSuat.id") Integer congSuatId,
            @RequestParam("chatLieuCanh.id") Integer chatLieuCanhId,
            @RequestParam("duongKinhCanh.id") Integer duongKinhCanhId,
            @RequestParam("chatLieuKhung.id") Integer chatLieuKhungId,
            @RequestParam("deQuat.id") Integer deQuatId,
            @RequestParam("chieuCao.id") Integer chieuCaoId,
            @RequestParam("hang.id") Integer hangId,
            @RequestParam("cheDoGio.id") Integer cheDoGioId,
            @RequestParam("dieuKhienTuXa.id") Integer dieuKhienTuXaId,
            @RequestParam("hinhAnhFile") MultipartFile hinhAnhFile,
            RedirectAttributes redirectAttributes) {

        try {
            SanPhamChiTiet sanPhamChiTiet = sanPhamService.findById(sanPhamId);
            if (sanPhamChiTiet == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại!");
                return "redirect:/admin/san-pham";
            }

            SanPham sanPham = sanPhamChiTiet.getSanPham();
            sanPham.setTen(ten);
            sanPham.setMo_ta(moTa);
            sanPham.setTrang_thai(trangThai);
            sanPham.setKieuQuat(new KieuQuat(kieuQuatId));

            sanPhamChiTiet.setGia(gia);
            sanPhamChiTiet.setGia_nhap(giaNhap);
            sanPhamChiTiet.setSo_luong(soLuong);
            sanPhamChiTiet.setMauSac(new MauSac(mauSacId));
            sanPhamChiTiet.setNutBam(new NutBam(nutBamId));
            sanPhamChiTiet.setCongSuat(new CongSuat(congSuatId));
            sanPhamChiTiet.setChatLieuCanh(new ChatLieuCanh(chatLieuCanhId));
            sanPhamChiTiet.setDuongKinhCanh(new DuongKinhCanh(duongKinhCanhId));
            sanPhamChiTiet.setChatLieuKhung(new ChatLieuKhung(chatLieuKhungId));
            sanPhamChiTiet.setDeQuat(new DeQuat(deQuatId));
            sanPhamChiTiet.setChieuCao(new ChieuCao(chieuCaoId));
            sanPhamChiTiet.setHang(new Hang(hangId));
            sanPhamChiTiet.setCheDoGio(new CheDoGio(cheDoGioId));
            sanPhamChiTiet.setDieuKhienTuXa(new DieuKhienTuXa(dieuKhienTuXaId));

            if (!hinhAnhFile.isEmpty()) {
                String fileName = hinhAnhFile.getOriginalFilename();
                sanPhamChiTiet.setHinh_anh(fileName);

                File file = new File("static/admin/images/" + fileName);
                hinhAnhFile.transferTo(file);
            }
            sanPham.setNgay_sua(new Date());
            sanPhamChiTiet.setNgay_sua(new Date());
            sanPhamChiTiet.setNguoi_sua("admin");

            sanPhamService.update(sanPhamChiTiet);
            redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật sản phẩm thất bại!");
        }

        return "redirect:/admin/san-pham";
    }


}
