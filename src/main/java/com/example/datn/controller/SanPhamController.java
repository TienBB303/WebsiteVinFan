package com.example.datn.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    HinhAnhRepo hinhAnhRepo;

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
            maxPrice = sanPhamService.getSanPhamGiaLonNhat();
        }
        Page<SanPhamChiTiet> searchPage = sanPhamService.searchProducts(query, minPrice, maxPrice, PageRequest.of(page, size));
        model.addAttribute("listSP", searchPage);
        model.addAttribute("query", query);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        return "admin/san_pham/san_pham_index";
    }



    @GetMapping("/san-pham/viewAdd")
    public String viewAddProduct() {
        return "admin/san_pham/san_pham_add";
    }

    @PostMapping("/san-pham/add-bien-the")
    public ResponseEntity<?> addProduct(
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
        String ma = sanPhamService.taoMaTuDong();  // tự động gen mã
        sanPhamTam.setMa(ma);
        sanPhamTam.setTen(ten);
        sanPhamTam.setKieuQuat(kieuQuatRepo.findById(kieuQuatId).orElse(null));
        sanPhamTam.setTrang_thai(true);
        sanPhamTam.setNgay_tao(new Date());

        List<String> existingProductNames = sanPhamRepo.getTenTonTai();

        List<SanPhamChiTietTam> listSPCTTam = new ArrayList<>();

        for (Integer mauSacId : mauSacIds) {
            MauSac mauSac = mauSacRepo.findById(mauSacId).orElse(null);
            for (Integer congSuatId : congSuatIds) {
                CongSuat congSuat = congSuatRepo.findById(congSuatId).orElse(null);
                for (Integer cheDoGioId : cheDoGioIds) {
                    CheDoGio cheDoGio = cheDoGioRepo.findById(cheDoGioId).orElse(null);
                    // check trung thuộc tính, cho phép trùng tên
                    if (sanPhamService.checkTrungLap(ten, congSuat, mauSac)) {
                        return ResponseEntity.badRequest().body(
                                "Sản phẩm " + ten + " đã tồn tại với công suất: " + congSuat.getTen() + " và màu sắc: " + mauSac.getTen()
                        );
                    }
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
        return ResponseEntity.ok(listSPCTTam);
    }

    // Nhận giá trị từ sản phẩm tạm
    @PostMapping("/san-pham/confirm")
    public ResponseEntity<String> confirmProducts(
            @RequestParam(value = "gia", required = false) List<BigDecimal> gias,
            @RequestParam(value = "so_luong", required = false) List<Integer> soLuongs,
            HttpSession session, Model model) {

        List<SanPhamChiTietTam> sanPhamChiTietTamList = (List<SanPhamChiTietTam>) session.getAttribute("listSPCTTam");

        // Kiểm tra sản phẩm tạm null hay không
        if (sanPhamChiTietTamList == null || sanPhamChiTietTamList.isEmpty()) {
            return ResponseEntity.badRequest().body("Không có sản phẩm để xác nhận.");
        }

        // Cập nhật giá và số lượng cho từng sản phẩm tạm khi update ở bảng
        for (int i = 0; i < sanPhamChiTietTamList.size(); i++) {
            SanPhamChiTietTam spTam = sanPhamChiTietTamList.get(i);
            BigDecimal gia = gias.get(i);
            Integer soLuong = soLuongs.get(i);

            // Kiểm tra điều kiện cho giá và số lượng
            if (gia.compareTo(new BigDecimal(10000)) < 0) {
                return ResponseEntity.badRequest().body("Giá sản phẩm phải lớn hơn 10.000.");
            }
            if (soLuong < 0 || soLuong > 500) {
                return ResponseEntity.badRequest().body("Số lượng phải nằm trong khoảng 0 - 500.");
            }
            if (soLuong <= 0) {
                spTam.setTrang_thai(false); // Tắt trạng thái nếu hết hàng
            } else {
                spTam.setTrang_thai(true); // Bật trạng thái nếu còn hàng
            }
            // Cập nhật giá và số lượng
            spTam.setGia(gia);
            spTam.setSo_luong(soLuong);
        }

        // confirm
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
            sanPhamChiTiet.setChatLieuCanh(spTam.getChatLieuCanh());
            sanPhamChiTiet.setHang(spTam.getHang());
            sanPhamChiTiet.setDieuKhienTuXa(spTam.getDieuKhienTuXa());

            sanPhamChiTiet.setGia(spTam.getGia());
            sanPhamChiTiet.setSo_luong(spTam.getSo_luong());
            sanPhamChiTiet.setTrang_thai(spTam.getTrang_thai());
            sanPhamChiTiet.setNgay_tao(spTam.getNgay_tao());
            sanPhamChiTiet.setNguoi_tao(spTam.getNguoi_tao());

            if (sanPhamService.motSanPhamTrangThaiOn(sanPham.getId())) {
                sanPham.setTrang_thai(true); // Tắt sản phẩm nếu tất cả biến thể đều tắt
            } else {
                sanPham.setTrang_thai(false); // Bật sản phẩm nếu còn ít nhất 1 biến thể bật
            }
            listSPCT.add(sanPhamChiTiet);
        }

        // lưu vào db sản phẩm + SPCT
        sanPhamService.create(sanPham, listSPCT);

        // Xóa list sp tạm
        session.removeAttribute("listSPCTTam");
        return ResponseEntity.ok("Sản phẩm đã được xác nhận thành công.");
    }

    //    Chuyển trang update
    @GetMapping("/san-pham/viewUpdate/{id}")
    public String viewUpdateProduct(@PathVariable("id") Long id, Model model) {
        SanPhamChiTiet sanPhamChiTiet = sanPhamService.findById(id);
        if (sanPhamChiTiet == null) {
            return "redirect:/admin/san-pham";
        }
        model.addAttribute("spUpdate", sanPhamChiTiet);
        return "admin/san_pham/san_pham_update";
    }

    //  Cập nhật sản phẩm
    @PostMapping("/san-pham/update")
    public String updateProduct(
            @RequestParam("id") Long sanPhamId,
            @RequestParam("sanPham.ten") String ten,
            @RequestParam("sanPham.mo_ta") String moTa,
            @RequestParam("gia") String giaStr,  // Đổi kiểu sang String để xử lý
            @RequestParam("so_luong") Integer soLuong,
            @RequestParam("trang_thai") Boolean trangThai,
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
//            @RequestParam("hinhAnhFile") MultipartFile hinhAnhFile,
            RedirectAttributes redirectAttributes) {

        try {
            String sanitizedInput = giaStr
                    .replaceAll("[^\\d]", "");

            BigDecimal gia = new BigDecimal(sanitizedInput);

            SanPhamChiTiet sanPhamChiTiet = sanPhamService.findById(sanPhamId);
            if (sanPhamChiTiet == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại!");
                return "redirect:/admin/san-pham";
            }

            SanPham sanPham = sanPhamChiTiet.getSanPham();
            sanPham.setTen(ten);
            sanPham.setMo_ta(moTa);
            sanPham.setNgay_sua(new Date());
            sanPham.setKieuQuat(sanPham.getKieuQuat());

            sanPhamChiTiet.setGia(gia);
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
            sanPhamChiTiet.setNgay_sua(new Date());
            sanPhamChiTiet.setTrang_thai(trangThai);

            sanPhamChiTiet.setNguoi_sua("admin");
            sanPhamService.update(sanPhamChiTiet);

            if (soLuong <= 0) {
                sanPhamChiTiet.setTrang_thai(false); // Tắt trạng thái nếu hết hàng
            } else {
                sanPhamChiTiet.setTrang_thai(true); // Bật trạng thái nếu còn hàng
            }

            if (sanPhamService.motSanPhamTrangThaiOn(sanPham.getId())) {
                sanPham.setTrang_thai(true); // Tắt sản phẩm nếu tất cả biến thể đều tắt
            } else {
                sanPham.setTrang_thai(false); // Bật sản phẩm nếu còn ít nhất 1 biến thể bật
            }
            sanPhamService.update(sanPham);

            redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật sản phẩm thất bại!");
        }
        return "redirect:/admin/san-pham";
    }


//    @DeleteMapping("/delete-tam/{id}")
//    @ResponseBody
//    public ResponseEntity<Void> deleteSanPhamTam(@PathVariable Long id, HttpSession session) {
//        try {
//            List<SanPhamChiTietTam> sanPhamChiTietTamList = (List<SanPhamChiTietTam>) session.getAttribute("listSPCTTam");
//            if (sanPhamChiTietTamList != null) {
//                sanPhamChiTietTamList.removeIf(spTam -> spTam.getId().equals(id));
//                session.setAttribute("listSPCTTam", sanPhamChiTietTamList);
//            }
//            return ResponseEntity.noContent().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}
