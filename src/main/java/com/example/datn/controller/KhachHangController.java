package com.example.datn.controller;


import com.example.datn.entity.DiaChi;
import com.example.datn.entity.KhachHang;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.DiaChiRepository;
import com.example.datn.repository.KhachHangRepo;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.service.EmailService;
import com.example.datn.service.khach_hang_service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangRepo khachHangRepo;

    @Autowired
    KhachHangService khachHangService;

    @Autowired
    EmailService emailService;

    @Autowired
    private DiaChiRepository diaChiRepository;

    @Autowired
    NhanVienRepository nhanVienRepository;

    @GetMapping("/index")
    public String loadTable(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "trang_thai", defaultValue = "") Boolean trang_thai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model){
        if (page < 0) {
            page = 0;
        }

        PageRequest pageable = PageRequest.of(page, size);
        Page<KhachHang> khachHangPage = khachHangService.search(keyword.trim(), trang_thai, pageable);
//        List<KhachHang> locListKhachHang = khachHangPage.getContent().stream()
//                .filter(kh -> kh.getId() != 1)
//                .collect(Collectors.toList());
//
//        Page<KhachHang> pageKhachhang = new PageImpl<>(locListKhachHang, pageable, locListKhachHang.size());
        model.addAttribute("listsKhachhang", khachHangPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("trang_thai", trang_thai != null ? trang_thai : "");
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo", nv);
        return "/admin/khach-hang/index";
    }


    @GetMapping("/from-them")
    public String fromThem(Model model) {
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo", nv);
        return "/admin/khach-hang/add";
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(
            @RequestParam("ten") String ten,
            @RequestParam("email") String email,
            @RequestParam("matKhau") String matKhau,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam("ngaySinh") @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date ngaySinh,
            @RequestParam("gioiTinh") String gioiTinh,
            @RequestParam("tinhThanhPho") String tinhThanhPho,
            @RequestParam("quanHuyen") String quanHuyen,
            @RequestParam("xaPhuong") String xaPhuong,
            @RequestParam("soNhaNgoDuong") String soNhaNgoDuong) {
        try {
            KhachHang kh = new KhachHang();
            kh.setMa(khachHangService.taoMaTuDong());
            if (ten == null || ten.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên khách hàng không được để trống.");
            }
            kh.setTen(ten);
            if (ten == null || ten.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên khách hàng không được để trống.");
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email không được để trống.");
            }
            if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                return ResponseEntity.badRequest().body("Email không đúng định dạng.");
            }
            kh.setEmail(email);

            if (matKhau == null || matKhau.trim().isEmpty() || matKhau.length() < 6) {
                return ResponseEntity.badRequest().body("Mật khẩu không được để trống và nhỏ hơn 6 ký tự.");
            }
            kh.setMatKhau(matKhau);

            if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Số điện thoại không được để trống.");
            }

            if (!soDienThoai.matches("^0[0-9]{8,9}$")) {
                return ResponseEntity.badRequest().body("Số điện thoại phải bắt đầu bằng số 0 và có 9 hoặc 10 chữ số.");
            }
            kh.setSoDienThoai(soDienThoai.trim());

            kh.setGioiTinh(gioiTinh);

            LocalDate now = LocalDate.now();
            LocalDate birthDate = ngaySinh.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (Period.between(birthDate, now).getYears() < 10) {
                return ResponseEntity.badRequest().body("Khách hàng cần đủ 10 tuổi để đăng kí mua hàng.");
            }
            kh.setNgaySinh(ngaySinh);
            NhanVien nv = nhanVienRepository.profileNhanVien();
            kh.setNguoiTao(nv.getTen());
            kh.setNgayTao(new Date());
            kh.setTrangThai(true);

            khachHangRepo.save(kh);
            //đại chỉ KH
            DiaChi diaChi = new DiaChi();
            diaChi.setKhachHang(kh);
            diaChi.setTinhThanhPho(tinhThanhPho);
            diaChi.setQuanHuyen(quanHuyen);
            diaChi.setXaPhuong(xaPhuong);
            diaChi.setSoNhaNgoDuong(soNhaNgoDuong);
            diaChi.setTrangThai(true);
            diaChiRepository.save(diaChi);
            //Gửi mail
            emailService.sendEmail(kh.getEmail(), "Tạo tài khoản thành công", kh.getEmail(), kh.getMatKhau());
            return ResponseEntity.ok().body( "Thêm khách hàng thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Đã xảy ra lỗi trong quá trình thêm khách hàng.");
        }
    }

    @GetMapping("/from-sua/{id}")
    public String fromSua(Model model, @PathVariable("id") Integer id) {
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(id);  // lấy danh sách địa chỉ
        diaChiList.sort((d1, d2) -> Boolean.compare(
                d2.getTrangThai() != null ? d2.getTrangThai() : false,
                d1.getTrangThai() != null ? d1.getTrangThai() : false
        ));
        model.addAttribute("khachHang", khachHangRepo.findById(id).orElse(null));
        model.addAttribute("diaChiList", diaChiList);
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo", nv);
        return "/admin/khach-hang/sua";  // trả về trang Thymeleaf
    }

    @PostMapping("/update")
    public ResponseEntity<?> suaKhachHang(
            @RequestParam("id") Long id,
            @RequestParam("ten") String ten,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam("ngaySinh") @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date ngaySinh,
            @RequestParam("gioiTinh") String gioiTinh,
            @RequestParam("trangThai") Boolean trangThai){
        try {
            KhachHang kh = khachHangRepo.findById(id).orElse(null);
            if (ten == null || ten.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên khách hàng không được để trống.");
            }
            kh.setTen(ten);
            if ((soDienThoai == null || soDienThoai.trim().isEmpty())){
                return ResponseEntity.badRequest().body("Số điện thoại không được để trống.");
            }
            if (soDienThoai != null && !soDienThoai.trim().isEmpty() && !soDienThoai.trim().equals(kh.getSoDienThoai())) {
                if (!soDienThoai.matches("^0[0-9]{8,9}$")) {
                    return ResponseEntity.badRequest().body("Số điện thoại phải bắt đầu bằng số 0 và có 9 hoặc 10 chữ số.");
                }
                if (nhanVienRepository.existsBySoDienThoai(soDienThoai.trim())) {
                    return ResponseEntity.badRequest().body("Số điện thoại đã tồn tại. Vui lòng sử dụng số khác.");
                }
                kh.setSoDienThoai(soDienThoai.trim());
            }
            LocalDate now = LocalDate.now();
            LocalDate birthDate = ngaySinh.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (Period.between(birthDate, now).getYears() < 18) {
                return ResponseEntity.badRequest().body("Khách hàng phải đủ 18 tuổi để mua sắm trực tuyến hợp pháp.");
            }
            kh.setNgaySinh(ngaySinh);
            kh.setGioiTinh(gioiTinh);
            kh.setTrangThai(trangThai);

            kh.setNgaySua(new Date());
            khachHangRepo.save(kh);
            return ResponseEntity.ok().body("Sửa khách hàng thành công.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Đã xảy ra lỗi trong quá trình thêm khách hàng.");
        }
    }

    @PostMapping("/them-dia-chi")
    public String ThemDiaChi(@RequestParam("khachHangId") Integer khachHangId,
                             @RequestParam("tinhThanhPho") String tinhThanhPho,
                             @RequestParam("quanHuyen") String quanHuyen,
                             @RequestParam("xaPhuong") String xaPhuong,
                             @RequestParam("soNhaNgoDuong") String soNhaNgoDuong) {
        KhachHang khachHang = khachHangRepo.findById(khachHangId).orElseThrow(() -> new IllegalArgumentException("Khách hàng không tồn tại"));
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(khachHangId);
        for (DiaChi diaChi: diaChiList) {
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

        return "redirect:/admin/khach-hang/from-sua/"+khachHangId;  // Chuyển hướng về trang sửa khách hàng
    }
    @PostMapping("/dia-chi-mac-dinh")
    public String suaDiaChiMacDinh(@RequestParam("khachHangId") Integer khachHangId,
                                   @ModelAttribute("diaChiId") Integer diaChiId){
        List<DiaChi> diaChiList = diaChiRepository.findByKhachHangId(khachHangId);
        for (DiaChi diaChi: diaChiList) {
            if (diaChi.getId().equals(diaChiId)) {
                diaChi.setTrangThai(true); // Địa chỉ được chọn làm mặc định
            } else {
                diaChi.setTrangThai(false); // Các địa chỉ khác không phải là mặc định
            }
            diaChiRepository.save(diaChi); // Lưu vào database
        }
        return "redirect:/admin/khach-hang/from-sua/"+khachHangId;
    }
    @GetMapping("/xoa/{id}")
    public String xoa(@RequestParam("khachHangId") Integer khachHangId,
                      @PathVariable("id") Integer id){
        diaChiRepository.deleteById(id);
        return "redirect:/admin/khach-hang/from-sua/"+khachHangId;
    }


}
