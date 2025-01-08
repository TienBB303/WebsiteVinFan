package com.example.datn.controller.SanPham;

import com.example.datn.entity.*;
import com.example.datn.entity.thuoc_tinh.*;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.repository.SanPhamRepo.SanPhamRepo;
import com.example.datn.repository.ThuocTinhRepo.*;
import com.example.datn.service.CloudinaryService;
import com.example.datn.service.SanPhamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

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
                                 @RequestParam(value = "trang_thai", defaultValue = "") Boolean trang_thai,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        BigDecimal minPrice = epKieuDecimal(minPriceStr.trim());
        BigDecimal maxPrice = epKieuDecimal(maxPriceStr.trim());
        if (maxPrice.compareTo(BigDecimal.ZERO) == 0) {
            maxPrice = sanPhamService.getSanPhamGiaLonNhat();
        }
        Page<SanPhamChiTiet> searchPage = sanPhamService.searchProducts(query.trim(), minPrice, maxPrice, trang_thai, PageRequest.of(page, size));
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo", nv);
        model.addAttribute("listSP", searchPage);
        model.addAttribute("query", query);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("trang_thai", trang_thai != null ? trang_thai : "");
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
    public String viewAddProduct(Model model) {
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo", nv);
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
            @RequestParam("hinhAnh.hinh_anh_1") MultipartFile file,
            @ModelAttribute NhanVien nhanVien,
            HttpSession session, Model model) {
        try {
            if (inputMa != null && inputMa.length() > 7) {
                return ResponseEntity.badRequest().body("Mã sản phẩm không được vượt quá 7 ký tự.");
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
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Sản phẩm phải có 1 ảnh.");
            }
            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();
                if (contentType == null ||
                        (!contentType.equals("image/png") &&
                                !contentType.equals("image/jpeg") &&
                                !contentType.equals("image/jpg"))) {
                    return ResponseEntity.badRequest().body("File ảnh phải có định dạng PNG, JPG hoặc JPEG.");
                }

                Map<String, String> uploadAnh = cloudinaryService.upload(file);
                String imageUrl = uploadAnh.get("url");

                HinhAnh hinhAnh = new HinhAnh();
                hinhAnh.setHinh_anh_1(imageUrl);
                hinhAnhRepo.save(hinhAnh);

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
                        sanPhamChiTietTam.setHinhAnh(hinhAnh); //thêm ảnh
                        sanPhamChiTietTam.setGia(new BigDecimal(100000));
                        sanPhamChiTietTam.setSo_luong(1);
                        sanPhamChiTietTam.setTrang_thai(true);
                        sanPhamChiTietTam.setNgay_tao(new Date());
                        sanPhamChiTietTam.setNguoi_tao(nhanVien.getTen());

                        listSPCTTam.add(sanPhamChiTietTam);
                    }
                }
            }

            session.setAttribute("listSPCTTam", listSPCTTam);
            System.out.println("list : " + listSPCTTam.size());
            model.addAttribute("listSPCTTam", listSPCTTam);
            return ResponseEntity.ok(listSPCTTam);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("lỗi");
        }


//        Map<String, List<SanPhamChiTietTam>> productsByColor = new HashMap<>();
//        for (SanPhamChiTietTam product : listSPCTTam) {
//            String color = product.getMauSac().getTen();
//            productsByColor.putIfAbsent(color, new ArrayList<>());
//            productsByColor.get(color).add(product);
//        }
//
//        session.setAttribute("listSPCTTam", listSPCTTam);
//        session.setAttribute("productsByColor", productsByColor);
//        model.addAttribute("productsByColor", productsByColor);

    }
    // Nhận giá trị từ sản phẩm tạm
    @PostMapping("/san-pham/confirm")
    public ResponseEntity<String> confirmProducts(
            @RequestParam(value = "gia") List<String> giasStr,
            @RequestParam(value = "so_luong") List<String> soLuongsStr,
            HttpSession session) {

        List<SanPhamChiTietTam> sanPhamChiTietTamList = (List<SanPhamChiTietTam>) session.getAttribute("listSPCTTam");
        System.out.println(giasStr.size());
        if (sanPhamChiTietTamList == null || sanPhamChiTietTamList.isEmpty()) {
            return ResponseEntity.badRequest().body("Không có sản phẩm để xác nhận.");
        }
        if (giasStr == null || giasStr.size() != sanPhamChiTietTamList.size()) {
            return ResponseEntity.badRequest().body("Giá không được để trống.");
        }
        if (soLuongsStr == null || soLuongsStr.size() != sanPhamChiTietTamList.size()) {
            return ResponseEntity.badRequest().body("Số lượng không được để trống.");
        }
        for (int i = 0; i < sanPhamChiTietTamList.size(); i++) {
            SanPhamChiTietTam spTam = sanPhamChiTietTamList.get(i);
            BigDecimal gia = epKieuDecimal(giasStr.get(i));
            Integer soLuong = null;
            try {
                soLuong = Integer.parseInt(soLuongsStr.get(i));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Lỗi tại sản phẩm " + (i + 1) + ": Số lượng phải nằm trong khoảng 0 - 500.");
            }
            if (gia == null) {
                return ResponseEntity.badRequest().body("Lỗi tại sản phẩm " + (i + 1) + ": Giá không được để trống.");
            }
            if (soLuong == null) {
                return ResponseEntity.badRequest().body("Lỗi tại sản phẩm " + (i + 1) + ": Số lượng không được để trống.");
            }
            if (gia.compareTo(new BigDecimal(10000)) < 0) {
                return ResponseEntity.badRequest().body("Lỗi tại sản phẩm " + (i + 1) + ": Giá sản phẩm phải lớn hơn 10.000.");
            } else if (gia.compareTo(new BigDecimal(20000000)) > 0) {
                return ResponseEntity.badRequest().body("Lỗi tại sản phẩm " + (i + 1) + ": Giá sản phẩm không vượt quá 20 triệu.");
            }
            if (soLuong < 0 || soLuong > 500) {
                return ResponseEntity.badRequest().body("Lỗi tại sản phẩm " + (i + 1) + ": Số lượng phải nằm trong khoảng 0 - 500.");
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
                sanPhamChiTiet.setHinhAnh(spTam.getHinhAnh());
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
                sanPhamChiTiet.setHinhAnh(spTam.getHinhAnh());
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
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo", nv);
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
            @RequestParam("gia") String giaStr,
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
            BigDecimal gia = epKieuDecimal(giaStr.trim());
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
            sanPhamChiTiet.setTrang_thai(trangThai);
            // Kiểm tra trạng thái
//            if (soLuong == 0) {
//                sanPhamChiTiet.setTrang_thai(false);
//            } else {
//                sanPhamChiTiet.setTrang_thai(trangThai);
//            }
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
    }
