package com.example.datn.controller.sale_on_controller;

import com.example.datn.entity.*;
import com.example.datn.entity.phieu_giam.PhieuGiam;
import com.example.datn.entity.thuoc_tinh.HinhAnh;

import com.example.datn.repository.DiaChiRepository;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.repository.LichSuHoaDonRepo;

import com.example.datn.repository.ThuocTinhRepo.HinhAnhRepo;
import com.example.datn.repository.ThuocTinhRepo.KieuQuatRepo;
import com.example.datn.repository.phieu_giam_repo.PhieuGiamRepo;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vin-fan")
public class ProductCatalog {

    private final HoaDonService hoaDonService;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
    //    @Autowired
//    private PhieuGiamSanPhamRepo phieuGiamSanPhamRepo;
    @Autowired
    private PhieuGiamRepo phieuGiamRepo;
    @Autowired
    private SPCTRepo spctRepo;
    @Autowired
    private KieuQuatRepo kieuQuatRepo;

    @Autowired
    private KhachHangRepo khachHangRepo;
    @Autowired
    private HinhAnhRepo hinhAnhRepo;

    @Autowired
    private DiaChiRepository diaChiRepository;


    @GetMapping("/danh-muc")
    public String productCatalog(
            Model model,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "kieuQuatId", required = false) Integer kieuQuatId,
            @RequestParam(value = "maSanPham", required = false) String maSanPham,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "newest") String sortOrder,
            @RequestParam(value = "filter", required = false, defaultValue = "all") String filter) {

        List<SanPhamChiTiet> sanPhamKhongGiamGia = new ArrayList<>();
        List<PhieuGiam> sanPhamGiamGia = new ArrayList<>();

        // Logic xử lý
        if (query != null && !query.trim().isEmpty()) {
            sanPhamGiamGia = phieuGiamRepo.timKiemSanPhamCoGiamGia(query);
            List<Long> sanPhamCoGiamGiaIds = sanPhamGiamGia.stream()
                    .map(pg -> pg.getSpct().getId())
                    .collect(Collectors.toList());
            sanPhamKhongGiamGia = spctRepo.timKiemTheoTen(query)
                    .stream()
                    .filter(sp -> !sanPhamCoGiamGiaIds.contains(sp.getId()))
                    .collect(Collectors.toList());
        } else if (maSanPham != null && !maSanPham.isEmpty()) {
            sanPhamKhongGiamGia = spctRepo.findBySanPhamMa(maSanPham);
            sanPhamGiamGia = phieuGiamRepo.findBySanPhamMa(maSanPham);
        } else {
            sanPhamGiamGia = phieuGiamRepo.findAllWithLinkedSpct();
            List<Long> sanPhamCoGiamGiaIds = sanPhamGiamGia.stream()
                    .map(pg -> pg.getSpct().getId())
                    .collect(Collectors.toList());
            sanPhamKhongGiamGia = spctRepo.findByIdNotIn(sanPhamCoGiamGiaIds);
        }

        if ("discounted".equalsIgnoreCase(filter)) {
            sanPhamKhongGiamGia.clear();
        } else if ("non-discounted".equalsIgnoreCase(filter)) {
            sanPhamGiamGia.clear();
        }
        // Xử lý sắp xếp cho sản phẩm không giảm giá
        if ("newest".equalsIgnoreCase(sortOrder)) {
            sanPhamKhongGiamGia.sort(Comparator.comparing(SanPhamChiTiet::getNgay_tao).reversed());
            sanPhamGiamGia.sort(Comparator.comparing(pg -> pg.getSpct().getNgay_tao(), Comparator.nullsLast(Comparator.reverseOrder())));
        } else if ("oldest".equalsIgnoreCase(sortOrder)) {
            sanPhamKhongGiamGia.sort(Comparator.comparing(SanPhamChiTiet::getNgay_tao));
            sanPhamGiamGia.sort(Comparator.comparing(pg -> pg.getSpct().getNgay_tao(), Comparator.nullsLast(Comparator.naturalOrder())));
        }

        // Lọc thêm
        if (kieuQuatId != null) {
            sanPhamKhongGiamGia = sanPhamKhongGiamGia.stream()
                    .filter(sp -> sp.getSanPham().getKieuQuat() != null && sp.getSanPham().getKieuQuat().getId().equals(kieuQuatId))
                    .collect(Collectors.toList());

            sanPhamGiamGia = sanPhamGiamGia.stream()
                    .filter(pg -> pg.getSpct().getSanPham().getKieuQuat() != null && pg.getSpct().getSanPham().getKieuQuat().getId().equals(kieuQuatId))
                    .collect(Collectors.toList());
        }

        // Lọc theo khoảng giá
        if (minPrice != null || maxPrice != null) {
            BigDecimal finalMinPrice = minPrice != null ? minPrice : BigDecimal.ZERO;
            BigDecimal finalMaxPrice = maxPrice != null ? maxPrice : BigDecimal.valueOf(Double.MAX_VALUE);

            sanPhamKhongGiamGia = sanPhamKhongGiamGia.stream()
                    .filter(sp -> sp.getGia() != null && sp.getGia().compareTo(finalMinPrice) >= 0 && sp.getGia().compareTo(finalMaxPrice) <= 0)
                    .collect(Collectors.toList());

            sanPhamGiamGia = sanPhamGiamGia.stream()
                    .filter(pg -> pg.getGiaSauGiam() != null && pg.getGiaSauGiam().compareTo(finalMinPrice) >= 0
                            && pg.getGiaSauGiam().compareTo(finalMaxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        // Loại bỏ sản phẩm trùng lặp theo mã sản phẩm
        sanPhamKhongGiamGia = sanPhamKhongGiamGia.stream()
                .filter(distinctByKey(sp -> sp.getSanPham().getMa() != null ? sp.getSanPham().getMa() : sp.getId()))
                .collect(Collectors.toList());

        sanPhamGiamGia = sanPhamGiamGia.stream()
                .filter(distinctByKey(pg -> pg.getSpct().getSanPham().getMa() != null ? pg.getSpct().getSanPham().getMa() : pg.getId()))
                .collect(Collectors.toList());
        KhachHang khachHang = khachHangRepo.profileKhachHang();
        model.addAttribute("khachHang", khachHang);


        model.addAttribute("sanPhamKhongGiamGia", sanPhamKhongGiamGia);
        model.addAttribute("sanPhamGiamGia", sanPhamGiamGia);
        model.addAttribute("query", query);
        model.addAttribute("kieuQuatId", kieuQuatId);
        model.addAttribute("maSanPham", maSanPham);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("filter", filter);
        model.addAttribute("kieuQuats", kieuQuatRepo.findAll());


        return "/admin/website/productCatalog";
    }

    // Helper method để lọc trùng lặp
    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    @GetMapping("/detail/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        // Tìm kiếm sản phẩm theo ID
        Optional<SanPhamChiTiet> sanPhamChiTietOpt = spctRepo.findById(id);

        if (sanPhamChiTietOpt.isEmpty()) {
            return "redirect:/cart/view"; // Redirect nếu sản phẩm không tồn tại
        }

        SanPhamChiTiet currentProduct = sanPhamChiTietOpt.get();

        // Lấy danh sách các biến thể của sản phẩm
        List<SanPhamChiTiet> bienTheList = spctRepo.findAllBySanPhamMa(currentProduct.getSanPham().getMa());

        // Lấy danh sách sản phẩm liên quan cùng kiểu quạt, loại bỏ sản phẩm hiện tại
        List<SanPhamChiTiet> sanPhamLienQuan = spctRepo.findBySanPhamKieuQuat(currentProduct.getSanPham().getKieuQuat().getId())
                .stream()
                .filter(sp -> sp != null
                        && sp.getSanPham() != null
                        && sp.getSanPham().getMa() != null
                        && !sp.getSanPham().getMa().equals(currentProduct.getSanPham().getMa())) // Loại bỏ sản phẩm cùng mã và kiểm tra null
                .collect(Collectors.groupingBy(sp -> sp.getSanPham().getMa())) // Nhóm theo mã sản phẩm
                .values().stream()
                .map(list -> list.get(0)) // Chọn đại diện đầu tiên của mỗi nhóm
                .filter(Objects::nonNull) // Loại bỏ phần tử null
                .collect(Collectors.toList());

        // Shuffle danh sách để ngẫu nhiên hóa
        Collections.shuffle(sanPhamLienQuan);

        // Giới hạn danh sách chỉ còn 4 sản phẩm
        if (sanPhamLienQuan.size() > 4) {
            sanPhamLienQuan = sanPhamLienQuan.subList(0, 4);
        }

        // Tạo một danh sách tổng hợp bao gồm cả bienTheList và sanPhamLienQuan
        List<SanPhamChiTiet> allProducts = new ArrayList<>();
        allProducts.addAll(bienTheList);
        allProducts.addAll(sanPhamLienQuan);

        // Tạo Map để lưu giá sau giảm cho tất cả các sản phẩm
        Map<Long, BigDecimal> giaSauGiamMap = allProducts.stream()
                .filter(sp -> sp != null && sp.getId() != null)
                .collect(Collectors.toMap(
                        SanPhamChiTiet::getId,
                        sp -> phieuGiamRepo.findBySanPhamChiTietId(sp.getId())
                                .map(PhieuGiam::getGiaSauGiam)
                                .orElse(sp.getGia()) // Nếu không có giảm giá, dùng giá gốc
                ));

        // Lấy biến thể đầu tiên làm mặc định
        SanPhamChiTiet defaultBienThe = bienTheList.isEmpty() ? null : bienTheList.get(0);
        BigDecimal defaultGiaSauGiam = (defaultBienThe != null) ? giaSauGiamMap.getOrDefault(defaultBienThe.getId(), defaultBienThe.getGia()) : BigDecimal.ZERO;

        // Truyền dữ liệu sang Thymeleaf
        model.addAttribute("sanPham", currentProduct.getSanPham());
        model.addAttribute("sanPhamChiTiet", currentProduct);
        model.addAttribute("bienTheList", bienTheList);
        model.addAttribute("giaSauGiamMap", giaSauGiamMap);
        model.addAttribute("defaultBienThe", defaultBienThe);
        model.addAttribute("defaultGiaSauGiam", defaultGiaSauGiam);
        model.addAttribute("sanPhamLienQuan", sanPhamLienQuan); // Truyền danh sách liên quan trực tiếp (tối đa 4 sản phẩm)

        return "admin/website/detailSP";
    }


    @GetMapping("/track-order")
    public String trackOrder(Model model) {
        KhachHang khachHang = khachHangRepo.profileKhachHang();

        if (khachHang == null) {
            model.addAttribute("errorMessage", "Chỉ khách hàng mới được phép truy cập thông tin đơn hàng!");
            return "redirect:/vin-fan/danh-muc"; // Trả về trang hiện tại để thông báo
        }

        List<HoaDon> hoaDons = hoaDonService.getHoaDonByIdKH(khachHang.getId());
        model.addAttribute("hoaDons", hoaDons);
        model.addAttribute("khachHang", khachHang);
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(khachHang.getId());  // lấy danh sách địa chỉ
        diaChiList.sort((d1, d2) -> Boolean.compare(
                d2.getTrangThai() != null ? d2.getTrangThai() : false,
                d1.getTrangThai() != null ? d1.getTrangThai() : false
        ));
        model.addAttribute("diaChiList", diaChiList);

        return "admin/website/trackOrder"; // Nếu là khách hàng, trả về trang thông tin đơn hàng
    }


    @GetMapping("/hoa-don-kh/{id}")
    public String detailOrder(@PathVariable long id, Model model) {
        //Lấy thông tin lịch sử hóa đơn theo id hóa đơn
        // Lấy thông tin hóa đơn
        Optional<HoaDon> hoaDonOptional = hoaDonService.findById(id);
        HoaDon hoaDon = hoaDonOptional.orElse(new HoaDon());
        model.addAttribute("hoaDon", hoaDon);

        List<LichSuHoaDon> lichSuHoaDonList = lichSuHoaDonRepo.findLichSuHoaDonByIdHoaDon(id);
        model.addAttribute("listHistory", lichSuHoaDonList);

        List<HoaDonChiTiet> listHDCT = this.hoaDonService.timSanPhamChiTietTheoHoaDon(id);
        model.addAttribute("listHDCT", listHDCT);

        KhachHang khachHang = khachHangRepo.profileKhachHang();
        model.addAttribute("khachHang", khachHang);

        return "admin/website/detailDH";
    }
    @PostMapping("/huy")
    public String huy(@ModelAttribute("id") long id) {
        hoaDonService.huyHoaDon(id);
        // Chuyển hướng người dùng đến trang chi tiết của HoaDon
        return "redirect:/vin-fan/hoa-don-kh/" + id; // Chuyển hướng với tham số id
    }

    @GetMapping("/spct/images/{id}")
    @ResponseBody
    public Map<String, Object> getImagesBySpctId(@PathVariable Long id) {
        SanPhamChiTiet spct = spctRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Lấy danh sách hình ảnh liên quan đến SPCT
        List<HinhAnh> danhSachHinhAnh = hinhAnhRepo.findAllBySanPhamChiTietId(spct.getId());
        List<String> imageUrls = danhSachHinhAnh.stream()
                .map(HinhAnh::getHinh_anh_1) // Sử dụng trường ảnh trong HinhAnh
                .collect(Collectors.toList());

        // Trả về JSON chứa danh sách ảnh
        Map<String, Object> response = new HashMap<>();
        response.put("images", imageUrls);
        response.put("soLuong", spct.getSo_luong());
        return response;
    }

    @PostMapping("/sua-khach-hang")
    public String sua(@ModelAttribute("khachHang") KhachHang khachHang) {
        KhachHang khachHang1 = khachHangRepo.profileKhachHang();
        LocalDate currentDate = LocalDate.now();
        Date sqlDate = Date.valueOf(currentDate);
        khachHang1.setNgaySua(sqlDate);
        khachHang1.setTen(khachHang.getTen());
        khachHang1.setSoDienThoai(khachHang.getSoDienThoai());
        khachHang1.setMatKhau(khachHang.getMatKhau());
        khachHang1.setNgaySinh(khachHang.getNgaySinh());
        khachHang1.setGioiTinh(khachHang.getGioiTinh());
        // Lưu khách hàng vào cơ sở dữ liệu
        khachHangRepo.save(khachHang1);
        return "redirect:/vin-fan/track-order";
    }

    @PostMapping("/sua-khach-hang/them-dia-chi")
    public String ThemDiaChi(@RequestParam("khachHangId") Integer khachHangId,
                             @RequestParam("tinhThanhPho") String tinhThanhPho,
                             @RequestParam("quanHuyen") String quanHuyen,
                             @RequestParam("xaPhuong") String xaPhuong,
                             @RequestParam("soNhaNgoDuong") String soNhaNgoDuong) {
        KhachHang khachHang = khachHangRepo.findById(khachHangId).orElseThrow(() -> new IllegalArgumentException("Khách hàng không tồn tại"));
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(khachHangId);
        for (DiaChi diaChi : diaChiList) {
            diaChi.setTrangThai(false); // Các địa chỉ khác không phải là mặc định
            diaChiRepository.save(diaChi); // Lưu vào database
        }
        DiaChi diaChi = new DiaChi();
        diaChi.setKhachHang(khachHang);  // Gán khách hàng đã có
        diaChi.setTinhThanhPho(tinhThanhPho);
        diaChi.setQuanHuyen(quanHuyen);
        diaChi.setXaPhuong(xaPhuong);
        diaChi.setSoNhaNgoDuong(soNhaNgoDuong);
        diaChi.setTrangThai(true);
        diaChiRepository.save(diaChi);

        return "redirect:/vin-fan/track-order";  // Chuyển hướng về trang sửa khách hàng
    }

    @PostMapping("/sua-khach-hang/dia-chi-mac-dinh")
    public String suaDiaChiMacDinh(@RequestParam("khachHangId") Integer khachHangId,
                                   @ModelAttribute("diaChiId") Integer diaChiId) {
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(khachHangId);
        for (DiaChi diaChi : diaChiList) {
            if (diaChi.getId().equals(diaChiId)) {
                diaChi.setTrangThai(true); // Địa chỉ được chọn làm mặc định
            } else {
                diaChi.setTrangThai(false); // Các địa chỉ khác không phải là mặc định
            }
            diaChiRepository.save(diaChi); // Lưu vào database
        }
        return "redirect:/vin-fan/track-order";
    }

    @GetMapping("/sua-khach-hang/xoa/{id}")
    public String xoa(@RequestParam("khachHangId") Integer khachHangId,
                      @PathVariable("id") Integer id) {
        diaChiRepository.deleteById(id);
        return "redirect:/vin-fan/track-order";
    }

}
