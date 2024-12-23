package com.example.datn.controller;

import com.example.datn.entity.*;
import com.example.datn.entity.thuoc_tinh.*;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.repository.SanPhamRepo;
import com.example.datn.repository.ThuocTinhRepo.*;
import com.example.datn.service.CloudinaryService;
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

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

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
//    @Autowired
//    DieuKhienTuXaRepo dieuKhienTuXaRepo;
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
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    NhanVienRepository nhanVienRepository;

    @ModelAttribute("listSanPham")
    public List<SanPham> listSanPham() {
        return sanPhamRepo.findAll();
    }

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

//    @ModelAttribute("listDieuKhienTuXa")
//    public List<DieuKhienTuXa> listDieuKhienTuXa() {
//        return dieuKhienTuXaRepo.findAll();
//    }

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
                                 @RequestParam(value = "minPrice", defaultValue = "0") String minPriceStr,
                                 @RequestParam(value = "maxPrice", defaultValue = "0") String maxPriceStr,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        BigDecimal minPrice = epKieuDecimal(minPriceStr.trim());
        BigDecimal maxPrice = epKieuDecimal(maxPriceStr.trim());
        if (maxPrice.compareTo(BigDecimal.ZERO) == 0) {
            maxPrice = sanPhamService.getSanPhamGiaLonNhat();
        }
        Page<SanPhamChiTiet> searchPage = sanPhamService.searchProducts(query.trim(), minPrice, maxPrice, PageRequest.of(page, size));
        model.addAttribute("listSP", searchPage);
        model.addAttribute("query", query);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        return "admin/san_pham/san_pham_index";
    }
    private BigDecimal epKieuDecimal(String priceStr) {
        if (priceStr.trim() == null || priceStr.isEmpty()) {
            return BigDecimal.ZERO;
        }
        String xoaString = priceStr.replaceAll(",", "");  // Loại bỏ dấu phẩy trong giá trị đầu vào
        try {
            return new BigDecimal(xoaString);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO; // Trả về 0 nếu không thể chuyển đổi
        }
    }


    @GetMapping("/san-pham/viewAdd")
    public String viewAddProduct() {
        return "admin/san_pham/san_pham_add";
    }

    @PostMapping("/san-pham/add-bien-the")
    public ResponseEntity<?> addProduct(
            @RequestParam("sanPham.ma") String inputMa,
            @RequestParam("sanPham.ten") String ten,
            @RequestParam("sanPham.kieuQuat.id") Integer kieuQuatId,
            @RequestParam("mauSac.id") List<Integer> mauSacIds,
            @RequestParam("congSuat.id") List<Integer> congSuatIds,
            @RequestParam("cheDoGio.id") Integer cheDoGioId,
            @RequestParam("nutBam.id") Integer nutBamId,
            @RequestParam("chatLieuCanh.id") Integer chatLieuCanhId,
            @RequestParam("duongKinhCanh.id") Integer duongKinhCanhId,
            @RequestParam("chatLieuKhung.id") Integer chatLieuKhungId,
            @RequestParam("deQuat.id") Integer deQuatId,
            @RequestParam("chieuCao.id") Integer chieuCaoId,
            @RequestParam("hang.id") Integer hangId,
            @ModelAttribute NhanVien nhanVien,
            HttpSession session, Model model) {

        if (inputMa != null && inputMa.length() > 5) {
            return ResponseEntity.badRequest().body("Mã sản phẩm không được vượt quá 5 ký tự.");
        }
        String ma = (inputMa == null || inputMa.trim().isEmpty()) ? sanPhamService.taoMaTuDong() : inputMa.trim();
        SanPham sp = sanPhamRepo.findByMa(ma);
        if (sp != null) {
            // Tên và kiểu quạt phải trùng
            if (!sp.getTen().trim().equalsIgnoreCase(ten.trim())) {
                return ResponseEntity.badRequest().body(
                        "Mã " + ma + " đã tồn tại, nhưng với tên: " + sp.getTen() + ". Vui lòng sử dụng cùng tên."
                );
            }
            if (!sp.getKieuQuat().getId().equals(kieuQuatId)) {
                return ResponseEntity.badRequest().body(
                        "Mã " + ma + " đã tồn tại, nhưng với kiểu quạt: " + sp.getKieuQuat().getTen() + ". Vui lòng chọn đúng kiểu quạt."
                );
            }
        }
        else {
            // Kiểm tra nếu mã không tồn tại, tên không được trùng với bất kỳ tên nào trong database
            List<SanPham> productsWithName = sanPhamRepo.findByTenIgnoreCase(ten.trim());
            SanPham spTimMa = sanPhamRepo.findByTen(ten.trim());
            if (!productsWithName.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        "Tên sản phẩm '" + ten + "' đã tồn tại trong hệ thống với mã là " + spTimMa.getMa()+" ."
                );
            }
        }
        List<SanPhamChiTiet> spcheck = spctRepo.timKiemTheoTen(ten.trim());
        if (spcheck != null && !spcheck.isEmpty()) {
            SanPhamChiTiet spDauTien = spcheck.get(0);
            if (!spDauTien.getCheDoGio().getId().equals(cheDoGioId)) {
                return ResponseEntity.badRequest().body("Bạn đang thực hiện bổ sung sản phẩm mã : " + ma + " chế độ gió phải là : " + spDauTien.getCheDoGio().getTen());
            }
            if (!spDauTien.getNutBam().getId().equals(nutBamId)) {
                return ResponseEntity.badRequest().body("Bạn đang thực hiện bổ sung sản phẩm mã : " + ma + " nút bấm phải là : " + spDauTien.getNutBam().getTen());
            }
            if (!spDauTien.getChatLieuCanh().getId().equals(chatLieuCanhId)) {
                return ResponseEntity.badRequest().body("Bạn đang thực hiện bổ sung sản phẩm mã : " + ma + " chất liệu cánh phải là : " + spDauTien.getChatLieuCanh().getTen());
            }
            if (!spDauTien.getDuongKinhCanh().getId().equals(duongKinhCanhId)) {
                return ResponseEntity.badRequest().body("Bạn đang thực hiện bổ sung sản phẩm mã : " + ma + " đường kính cánh phải là : " + spDauTien.getDuongKinhCanh().getTen());
            }
            if (!spDauTien.getChatLieuKhung().getId().equals(chatLieuKhungId)) {
                return ResponseEntity.badRequest().body("Bạn đang thực hiện bổ sung sản phẩm mã : " + ma + " chất liệu khung phải là : " + spDauTien.getChatLieuKhung().getTen());
            }
            if (!spDauTien.getDeQuat().getId().equals(deQuatId)) {
                return ResponseEntity.badRequest().body("Bạn đang thực hiện bổ sung sản phẩm mã : " + ma + " đế quạt cánh phải là : " + spDauTien.getDeQuat().getTen());
            }
            if (!spDauTien.getChieuCao().getId().equals(chieuCaoId)) {
                return ResponseEntity.badRequest().body("Bạn đang thực hiện bổ sung sản phẩm mã : " + ma + " chiều cao phải là : " + spDauTien.getChieuCao().getTen());
            }
            if (!spDauTien.getHang().getId().equals(hangId)) {
                return ResponseEntity.badRequest().body("Bạn đang thực hiện bổ sung sản phẩm mã : " + ma + " hãng phải là : " + spDauTien.getHang().getTen());
            }
        }

        // Cập nhật thông tin sản phẩm
        SanPhamTam sanPhamTam = new SanPhamTam();
        sanPhamTam.setMa(ma);
        sanPhamTam.setTen(ten);
        sanPhamTam.setKieuQuat(kieuQuatRepo.findById(kieuQuatId).orElse(null));
        sanPhamTam.setDieu_khien_tu_xa(true);

        sanPhamTam.setTrang_thai(true);
        sanPhamTam.setNgay_tao(new Date());

        List<SanPhamChiTietTam> listSPCTTam = new ArrayList<>();
        int idTuTang = 1;
        for (Integer mauSacId : mauSacIds) {
            MauSac mauSac = mauSacRepo.findById(mauSacId).orElse(null);
            for (Integer congSuatId : congSuatIds) {
                CongSuat congSuat = congSuatRepo.findById(congSuatId).orElse(null);
                // Kiểm tra trùng thuộc tính cho phép trùng tên
                if (sanPhamService.checkTrungLap(ten, congSuat, mauSac)) {
                    return ResponseEntity.badRequest().body(
                            "Sản phẩm " + ten + " đã tồn tại với công suất: " + congSuat.getTen() + " và màu sắc: " + mauSac.getTen()
                    );
                }
                SanPhamChiTietTam sanPhamChiTietTam = new SanPhamChiTietTam();
                sanPhamChiTietTam.setId((long) idTuTang++);
                sanPhamChiTietTam.setSanPhamTam(sanPhamTam);
                sanPhamChiTietTam.setMauSac(mauSac);
                sanPhamChiTietTam.setCongSuat(congSuat);
                sanPhamChiTietTam.setCheDoGio(cheDoGioRepo.findById(cheDoGioId).orElse(null));
                sanPhamChiTietTam.setNutBam(nutBamRepo.findById(nutBamId).orElse(null));
                sanPhamChiTietTam.setChatLieuCanh(chatLieuCanhRepo.findById(chatLieuCanhId).orElse(null));
                sanPhamChiTietTam.setDuongKinhCanh(duongKinhCanhRepo.findById(duongKinhCanhId).orElse(null));
                sanPhamChiTietTam.setChatLieuKhung(chatLieuKhungRepo.findById(chatLieuKhungId).orElse(null));
                sanPhamChiTietTam.setDeQuat(deQuatRepo.findById(deQuatId).orElse(null));
                sanPhamChiTietTam.setChieuCao(chieuCaoRepo.findById(chieuCaoId).orElse(null));
                sanPhamChiTietTam.setHang(hangRepo.findById(hangId).orElse(null));
                sanPhamChiTietTam.setGia(new BigDecimal(100000));
                sanPhamChiTietTam.setSo_luong(1);
                sanPhamChiTietTam.setTrang_thai(true);
                sanPhamChiTietTam.setNgay_tao(new Date());
                sanPhamChiTietTam.setNguoi_tao(nhanVien.getTen());

                listSPCTTam.add(sanPhamChiTietTam);
            }
        }

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
        if (gias.size() != sanPhamChiTietTamList.size()){
            return ResponseEntity.badRequest().body("Giá không được để trống.");
        }
        if (soLuongs.size() != sanPhamChiTietTamList.size()) {
            return ResponseEntity.badRequest().body("Số lượng không được để trống.");
        }
        // Cập nhật giá và số lượng cho từng sản phẩm tạm
        for (int i = 0; i < sanPhamChiTietTamList.size(); i++) {
            SanPhamChiTietTam spTam = sanPhamChiTietTamList.get(i);
            BigDecimal gia = gias.get(i);
            Integer soLuong = soLuongs.get(i);

            if (gia == null) {
                return ResponseEntity.badRequest().body("Giá không được để trống.");
            }
            if (soLuong == null) {
                return ResponseEntity.badRequest().body("Số lượng không được để trống.");
            }
            if (gia.compareTo(new BigDecimal(10000)) < 0) {
                return ResponseEntity.badRequest().body("Giá sản phẩm phải lớn hơn 10.000.");
            } else if (gia.compareTo(new BigDecimal(20000000)) > 0) {
                return ResponseEntity.badRequest().body("Giá sản phẩm không vượt quá 20 triệu.");
            }
            if (soLuong < 0 || soLuong > 500) {
                return ResponseEntity.badRequest().body("Số lượng phải nằm trong khoảng 0 - 500.");
            }
            if (soLuong <= 0) {
                spTam.setTrang_thai(false); // Tắt trạng thái nếu hết hàng
            } else {
                spTam.setTrang_thai(true); // Bật trạng thái nếu còn hàng
            }

            spTam.setGia(gia);
            spTam.setSo_luong(soLuong);
        }

        SanPham sanPham = sanPhamRepo.findByMa(sanPhamChiTietTamList.get(0).getSanPhamTam().getMa());

        if (sanPham != null) {
            List<SanPhamChiTiet> listSPCT = new ArrayList<>();

            for (SanPhamChiTietTam spTam : sanPhamChiTietTamList) {
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
                HinhAnh hinhAnh = new HinhAnh();
                sanPhamChiTiet.setHinhAnh(hinhAnhRepo.save(hinhAnh));
                sanPhamChiTiet.setGia(spTam.getGia());
                sanPhamChiTiet.setSo_luong(spTam.getSo_luong());
                sanPhamChiTiet.setTrang_thai(spTam.getTrang_thai());
                if(sanPhamChiTiet.getTrang_thai() == true){
                    sanPham.setTrang_thai(true);
                }
                sanPhamChiTiet.setNgay_tao(spTam.getNgay_tao());
                NhanVien nhanVien = nhanVienRepository.profileNhanVien();
                if (nhanVien == null) {
                    return ResponseEntity.badRequest().body("Không tìm thấy thông tin nhân viên cập nhật");
                }
                sanPhamChiTiet.setNguoi_tao(nhanVien.getTen());
                sanPhamService.update(sanPham);
                listSPCT.add(sanPhamChiTiet);
            }
            sanPhamService.create(sanPham, listSPCT);

            // Xóa list sp tạm
            session.removeAttribute("listSPCTTam");
            return ResponseEntity.ok("Bổ sung sản phẩm quạt thành công.");
        } else {
            SanPham sanPhamNew = new SanPham();
            List<SanPhamChiTiet> listSPCT = new ArrayList<>();
            for (SanPhamChiTietTam spTam : sanPhamChiTietTamList) {
                sanPhamNew.setTen(spTam.getSanPhamTam().getTen());
                sanPhamNew.setMa(spTam.getSanPhamTam().getMa());
                sanPhamNew.setKieuQuat(spTam.getSanPhamTam().getKieuQuat());
                sanPhamNew.setTrang_thai(spTam.getSanPhamTam().getTrang_thai());
                sanPhamNew.setDieu_khien_tu_xa(spTam.getSanPhamTam().getDieu_khien_tu_xa());
                sanPhamNew.setNgay_tao(spTam.getSanPhamTam().getNgay_tao());

                SanPhamChiTiet sanPhamChiTiet = new SanPhamChiTiet();
                sanPhamChiTiet.setSanPham(sanPhamNew);
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
                HinhAnh hinhAnh = new HinhAnh();
                sanPhamChiTiet.setHinhAnh(hinhAnhRepo.save(hinhAnh));

                sanPhamChiTiet.setGia(spTam.getGia());
                sanPhamChiTiet.setSo_luong(spTam.getSo_luong());
                sanPhamChiTiet.setTrang_thai(spTam.getTrang_thai());
                sanPhamChiTiet.setNgay_tao(spTam.getNgay_tao());
                NhanVien nhanVien = nhanVienRepository.profileNhanVien();
                if (nhanVien == null) {
                    return ResponseEntity.badRequest().body("Không tìm thấy thông tin nhân viên cập nhật");
                }
                sanPhamChiTiet.setNguoi_tao(nhanVien.getTen());

                listSPCT.add(sanPhamChiTiet);
            }
            sanPhamService.create(sanPhamNew, listSPCT);

            // Xóa list sp tạm
            session.removeAttribute("listSPCTTam");
            return ResponseEntity.ok("Sản phẩm mới đã được tạo và xác nhận thành công.");
        }
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
    public ResponseEntity<?> updateProduct(
            @RequestParam("id") Long sanPhamId,
            @RequestParam("sanPham.ten") String ten,
            @RequestParam("sanPham.mo_ta") String moTa,
            @RequestParam("sanPham.dieu_khien_tu_xa") Boolean dieuKhienTuXa,
            @RequestParam("gia") BigDecimal gia,
            @RequestParam("so_luong") Integer soLuong,
            @RequestParam("trang_thai") Boolean trangThai,
            @RequestParam("mauSac.id") Integer mauSacId,
            @RequestParam("congSuat.id") Integer congSuatId,
            @RequestParam("hinhAnh.hinh_anh_1") MultipartFile file) {

        try {
            // Lấy thông tin chi tiết sản phẩm
            SanPhamChiTiet sanPhamChiTiet = sanPhamService.findById(sanPhamId);
            if (sanPhamChiTiet == null) {
                return ResponseEntity.badRequest().body("Sản phẩm không tồn tại");
            }

            // check trùng thuộc tính đã có hoặc null
            CongSuat congSuatCheck = congSuatRepo.findById(congSuatId).orElse(null);
            MauSac mauSacCheck = mauSacRepo.findById(mauSacId).orElse(null);
            if (congSuatCheck == null || mauSacCheck == null) {
                return ResponseEntity.badRequest().body("Công suất hoặc màu sắc không hợp lệ");
            }
            if(ten.trim().isEmpty() || ten.trim() == null){
                return ResponseEntity.badRequest().body("Tên không được để trống");
            }
            boolean isNameChanged = !sanPhamChiTiet.getSanPham().getTen().equals(ten);
            boolean isCongSuatChanged = !sanPhamChiTiet.getCongSuat().getId().equals(congSuatId);
            boolean isMauSacChanged = !sanPhamChiTiet.getMauSac().getId().equals(mauSacId);

            if (isNameChanged || isCongSuatChanged || isMauSacChanged) {
                if (sanPhamService.checkTrungLap(ten, congSuatCheck, mauSacCheck)) {
                    return ResponseEntity.badRequest().body(
                            "Sản phẩm " + ten + " đã tồn tại với công suất: " + congSuatCheck.getTen() + " và màu sắc: " + mauSacCheck.getTen()
                    );
                }
            }
            //check tên trùng database
            SanPham sanPham = sanPhamChiTiet.getSanPham();

            if (isNameChanged) {
                List<SanPham> sanPhamTheoTen = sanPhamRepo.findByTenIgnoreCase(ten.trim());
                for (SanPham sp : sanPhamTheoTen) {
                    if (!sp.getId().equals(sanPham.getId())) { // Chỉ kiểm tra trùng nếu khác ID
                        return ResponseEntity.badRequest().body(
                                "Tên sản phẩm '" + ten + "' đã tồn tại trong hệ thống với mã là " + sp.getMa() + "."
                        );
                    }
                }
            }
            // Cập nhật thông tin sản phẩm chính
            sanPham.setTen(ten.trim());
            sanPham.setMo_ta(moTa);
            sanPham.setNgay_sua(new Date());
            sanPham.setDieu_khien_tu_xa(dieuKhienTuXa);

            // Cập nhật chi tiết sản phẩm
            sanPhamChiTiet.setMauSac(new MauSac(mauSacId));
            sanPhamChiTiet.setCongSuat(new CongSuat(congSuatId));
            sanPhamChiTiet.setNgay_sua(new Date());

            if (file != null && !file.isEmpty()) {
                Map<String, String> uploadAnh = cloudinaryService.upload(file);
                String imageUrl = uploadAnh.get("url");

                HinhAnh hinhAnh = sanPhamChiTiet.getHinhAnh();
                hinhAnh.setHinh_anh_1(imageUrl);
                HinhAnh savedHinhAnh = hinhAnhRepo.save(hinhAnh);
                sanPhamChiTiet.setHinhAnh(savedHinhAnh);
            }

            if (gia == null){
                return ResponseEntity.badRequest().body("Giá không được để trống");
            }
            if (soLuong == null){
                return ResponseEntity.badRequest().body("Số lượng không được để trống");
            }
            if (soLuong < 0 || soLuong > 500) {
                return ResponseEntity.badRequest().body("Số lượng phải nằm trong khoảng 0 - 500.");
            }
            if (gia.compareTo(new BigDecimal("10000")) < 0) {
                return ResponseEntity.badRequest().body("Giá không được nhỏ hơn 10.000");
            } else if (gia.compareTo(new BigDecimal("20000000")) > 0) {
                return ResponseEntity.badRequest().body("Giá không được lớn hơn 20.000.000");
            }
            // Cập nhật giá và số lượng
            sanPhamChiTiet.setGia(gia);
            sanPhamChiTiet.setSo_luong(soLuong);

            // Lấy thông tin người cập nhật
            NhanVien nhanVien = nhanVienRepository.profileNhanVien();
            if (nhanVien == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy thông tin nhân viên cập nhật");
            }
            sanPhamChiTiet.setNguoi_sua(nhanVien.getTen());

            // Kiểm tra trạng thái
            if (soLuong == 0) {
                sanPhamChiTiet.setTrang_thai(false);
            } else {
                sanPhamChiTiet.setTrang_thai(trangThai);
            }
            if (sanPhamService.motSanPhamTrangThaiOn(sanPham.getId())) {
                sanPham.setTrang_thai(true);
            } else {
                sanPham.setTrang_thai(false);
            }

            sanPhamService.update(sanPham);
            sanPhamService.update(sanPhamChiTiet);
            return ResponseEntity.ok().body(Map.of("message", "Cập nhật thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Cập nhật sản phẩm không thành công!");
        }
    }

//    @PostMapping("/delete-bien-the")
//    public String deleteBienThe(@RequestParam Long sanPhamTamId, RedirectAttributes redirectAttributes, HttpSession session) {
//        try {
//            List<SanPhamChiTietTam> sanPhamChiTietTamList = (List<SanPhamChiTietTam>) session.getAttribute("listSPCTTam");
//            sanPhamChiTietTamList.remove(sanPhamTamId);
//            redirectAttributes.addFlashAttribute("message", "Xóa biến thể thành công!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Xóa biến thể thất bại!");
//        }
//        return "admin/san-pham/viewAdd";
//    }
    @DeleteMapping("/delete-tam/{id}")
    public ResponseEntity<?> deleteSanPhamTam(@PathVariable Long id, HttpSession session) {
        if (id == null) {
            return ResponseEntity.badRequest().body("ID không hợp lệ!");
        }
        try {
            List<SanPhamChiTietTam> sanPhamChiTietTamList = (List<SanPhamChiTietTam>) session.getAttribute("listSPCTTam");
            System.out.println("Received delete request for ID: " + id);
            if (sanPhamChiTietTamList != null) {
                boolean removed = sanPhamChiTietTamList.removeIf(spTam -> spTam.getId().equals(id));
                if (removed) {
                    session.setAttribute("listSPCTTam", sanPhamChiTietTamList);
                    return ResponseEntity.ok("Xóa biến thể thành công!");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy biến thể với ID: " + id);
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Danh sách biến thể tạm rỗng!");
            }
            } catch (Exception e) {
                // Log lỗi để dễ theo dõi (có thể dùng logger thay vì in ra console)
                System.err.println("Lỗi khi xóa biến thể: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi xóa biến thể!");
            }
        }
    }
