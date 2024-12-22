package com.example.datn.controller;

//import com.example.datn.dto.request.SPCTUpdateRequest;
import com.example.datn.entity.*;
import com.example.datn.entity.thuoc_tinh.*;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.repository.SanPhamRepo;
import com.example.datn.repository.ThuocTinhRepo.*;
import com.example.datn.service.CloudinaryService;
import com.example.datn.service.SanPhamService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
            @RequestParam("sanPham.ma") String inputMa,
            @RequestParam("sanPham.ten") String ten,
//            @RequestParam("sanPham.anh") MultipartFile fileAnh,
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


        String ma = (inputMa == null || inputMa.trim().isEmpty())
                ? sanPhamService.taoMaTuDong() : inputMa.trim();
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
            List<SanPham> productsWithName = sanPhamRepo.findByTenIgnoreCase(ten);
            SanPham spTimMa = sanPhamRepo.findByTen(ten);
            if (!productsWithName.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        "Tên sản phẩm '" + ten + "' đã tồn tại trong hệ thống với mã là " + spTimMa.getMa()+" ."
                );
            }
        }

        // Cập nhật thông tin sản phẩm
        SanPhamTam sanPhamTam = new SanPhamTam();
        sanPhamTam.setMa(ma);
        sanPhamTam.setTen(ten);
        sanPhamTam.setKieuQuat(kieuQuatRepo.findById(kieuQuatId).orElse(null));
        sanPhamTam.setDieu_khien_tu_xa(true);

//        try {
//            if (fileAnh.isEmpty()) {
//                return ResponseEntity.badRequest().body("Lỗi khi tải lên ảnh.");
//            }
//
//            Map uploadResult = cloudinaryService.upload(fileAnh);
//            String imageUrl = (String) uploadResult.get("url");
//            if (imageUrl == null) {
//                return ResponseEntity.badRequest().body("Không thể lấy URL của ảnh.");
//            }
//            sanPhamTam.setHinh_anh(imageUrl);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Lỗi khi tải lên ảnh.");
//        }


        sanPhamTam.setTrang_thai(true);
        sanPhamTam.setNgay_tao(new Date());

        List<SanPhamChiTietTam> listSPCTTam = new ArrayList<>();

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
                HinhAnh hinhAnh = new HinhAnh();
                sanPhamChiTietTam.setHinhAnh(hinhAnhRepo.save(hinhAnh));
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
                sanPhamChiTiet.setHinhAnh(spTam.getHinhAnh());

                sanPhamChiTiet.setGia(spTam.getGia());
                sanPhamChiTiet.setSo_luong(spTam.getSo_luong());
                sanPhamChiTiet.setTrang_thai(spTam.getTrang_thai());
                if(sanPhamChiTiet.getTrang_thai() == true){
                    sanPham.setTrang_thai(true);
                }
                sanPhamChiTiet.setNgay_tao(spTam.getNgay_tao());
                sanPhamChiTiet.setNguoi_tao(spTam.getNguoi_tao());
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
                sanPhamNew.setHinh_anh(spTam.getSanPhamTam().getHinh_anh());
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
                sanPhamChiTiet.setNguoi_tao(spTam.getNguoi_tao());

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
    @PostMapping("/san-pham/update")
    public String updateProduct(
            @RequestParam("id") Long sanPhamId,
            @RequestParam("sanPham.ten") String ten,
            @RequestParam("sanPham.mo_ta") String moTa,
            @RequestParam("sanPham.dieu_khien_tu_xa") Boolean dieuKhienTuXa,
            @RequestParam("gia") String giaStr,
            @RequestParam("so_luong") Integer soLuong,
            @RequestParam("trang_thai") Boolean trangThai,
            @RequestParam("mauSac.id") Integer mauSacId,
            @RequestParam("congSuat.id") Integer congSuatId,
            @RequestParam("hinhAnh.hinh_anh_1") MultipartFile file,
            @ModelAttribute NhanVien nhanVien,
            Model model) {
        try {
            // Kiểm tra giá nhập
            String giaDinhDang = giaStr.replaceAll("[^\\d]", "");
            if (giaDinhDang.isEmpty()) {
                model.addAttribute("errorMessage", "Giá không hợp lệ!");
                return "redirect:/admin/san-pham/update"; // Hiển thị lại trang cập nhật
            }
            BigDecimal gia = new BigDecimal(giaDinhDang);

            // Kiểm tra số lượng
            if (soLuong < 0) {
                model.addAttribute("errorMessage", "Số lượng phải lớn hơn hoặc bằng 0!");
                return "redirect:/admin/san-pham/update"; // Hiển thị lại trang cập nhật
            }

            // Kiểm tra sự tồn tại của sản phẩm chi tiết
            SanPhamChiTiet sanPhamChiTiet = sanPhamService.findById(sanPhamId);
            if (sanPhamChiTiet == null) {
                model.addAttribute("errorMessage", "Sản phẩm không tồn tại!");
                return "admin/san_pham/san_pham_update"; // Trang cập nhật sản phẩm
            }
            // Kiểm tra tên sản phẩm
            String tenSanPham = ten.trim().toLowerCase();
            List<SanPham> tonTaiTen = sanPhamRepo.findByTenIgnoreCase(tenSanPham);
            if (!tonTaiTen.isEmpty() && tonTaiTen.stream().noneMatch(p -> p.getId().equals(sanPhamId))) {
                model.addAttribute("errorMessage", "Tên sản phẩm đã tồn tại trong hệ thống.");
                return "admin/san_pham/san_pham_update"; // Hiển thị lại trang cập nhật sản phẩm
            }

            // Cập nhật thông tin sản phẩm
            SanPham sanPham = sanPhamChiTiet.getSanPham();
            sanPham.setTen(ten);
            sanPham.setMo_ta(moTa);
            sanPham.setDieu_khien_tu_xa(dieuKhienTuXa);
            sanPham.setNgay_sua(new Date());

            sanPhamChiTiet.setMauSac(new MauSac(mauSacId));
            sanPhamChiTiet.setCongSuat(new CongSuat(congSuatId));
            sanPhamChiTiet.setGia(gia);
            sanPhamChiTiet.setSo_luong(soLuong);
            sanPhamChiTiet.setTrang_thai(soLuong > 0 ? trangThai : false);
            sanPhamChiTiet.setNguoi_sua(nhanVien.getTen());
            sanPhamChiTiet.setNgay_sua(new Date());

            // Upload hình ảnh
            if (file != null && !file.isEmpty()) {
                // Nếu có file mới, upload file lên cloud và cập nhật vào sản phẩm chi tiết
                Map<String, String> uploadAnh = cloudinaryService.upload(file);
                String imageUrl = uploadAnh.get("url");

                HinhAnh hinhAnh = sanPhamChiTiet.getHinhAnh();
                hinhAnh.setHinh_anh_1(imageUrl);
                hinhAnhRepo.save(hinhAnh);
            } else {
                // Nếu không có file mới, lấy ảnh từ sản phẩm chi tiết
                HinhAnh hinhAnh = sanPhamChiTiet.getHinhAnh();
                if (hinhAnh != null && hinhAnh.getHinh_anh_1() == null) {
                    // Nếu hình ảnh chi tiết cũng không có, giữ giá trị là null
                    hinhAnh.setHinh_anh_1(null);
                    hinhAnhRepo.save(hinhAnh);
                }
            }

            // Cập nhật trạng thái sản phẩm
            if (sanPhamService.motSanPhamTrangThaiOn(sanPham.getId())) {
                sanPham.setTrang_thai(true); // Tắt sản phẩm nếu tất cả biến thể đều tắt
            } else {
                sanPham.setTrang_thai(false); // Bật sản phẩm nếu còn ít nhất 1 biến thể bật
            }

            sanPhamService.update(sanPham);
            sanPhamService.update(sanPhamChiTiet);

            // Hiển thị thông báo thành công
            model.addAttribute("message", "Cập nhật sản phẩm thành công!");
            return "redirect:/admin/san-pham"; // Chuyển hướng tới trang danh sách sản phẩm

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Cập nhật sản phẩm thất bại!");
            return "redirect:/admin/san-pham/update"; // Hiển thị lại trang cập nhật với thông báo lỗi
        }
    }



    @DeleteMapping("/delete-tam/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteSanPhamTam(@PathVariable Long id, HttpSession session) {
        try {
            List<SanPhamChiTietTam> sanPhamChiTietTamList = (List<SanPhamChiTietTam>) session.getAttribute("listSPCTTam");
            if (sanPhamChiTietTamList != null) {
                sanPhamChiTietTamList.removeIf(spTam -> spTam.getId().equals(id));
                session.setAttribute("listSPCTTam", sanPhamChiTietTamList);
            }
            return ResponseEntity.ok("Xóa sản phẩm thành công.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa sản phẩm.");
        }
    }
}
