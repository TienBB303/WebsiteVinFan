package com.example.datn.controller;




import com.example.datn.entity.ChucVu;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.ChucVuRepository;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.service.CloudinaryService;
import com.example.datn.service.nhan_vien_service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

@Controller
@RequestMapping("/admin/nhan-vien/")
public class NhanVienController {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    NhanVienService nhanVienService;

    @Autowired
    private ChucVuRepository chucVuRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/index")
    public String loadTable(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "trang_thai", defaultValue = "") String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model){
        if (page < 0) {
            page = 0;
        }
        Boolean trang_thai = null;
        if ("1".equals(trangThai.trim())) {
            trang_thai = true;
        } else if ("0".equals(trangThai.trim())) {
            trang_thai = false;
        }
        PageRequest pageable = PageRequest.of(page, size);
        Page<NhanVien> nhanVienPage = nhanVienService.search(keyword.trim(), trang_thai, pageable);
        model.addAttribute("listsNhanVien", nhanVienPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("trang_thai", trang_thai);
        return "admin/nhan-vien/index";
    }

    @GetMapping("/view-add")
    public String formThemNhanVien(Model model) {
        NhanVien nhanVien = new NhanVien();
        List<ChucVu> chucVuList = chucVuRepository.findAll();
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("chucVuList", chucVuList);
        return "admin/nhan-vien/add";
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addNhanVien(
            @RequestParam("ten_nhan_vien") String ten_nhan_vien,
            @RequestParam("can_cuoc_cong_dan") String can_cuoc_cong_dan,
            @RequestParam("ngay_sinh") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngay_sinh,
            @RequestParam("gioi_tinh") Boolean gioi_tinh,
            @RequestParam("tinh_thanh_tho") String tinh_thanh_tho,
            @RequestParam("quan_huyen") String quan_huyen,
            @RequestParam("email") String email,
            @RequestParam("mat_khau") String mat_khau,
            @RequestParam("chuc_vu") Integer chuc_vu_id,
            @RequestParam("so_dien_thoai") String so_dien_thoai,
            @RequestParam("xa_phuong") String xa_phuong,
            @RequestParam("so_nha_ngo_duong") String so_nha_ngo_duong,
            @RequestParam("hinhAnh") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        NhanVien nhanVien = new NhanVien();
        try {
            if (file.isEmpty()) {
                response.put("message", "Nhân viên phải có một ảnh.");
                return ResponseEntity.badRequest().body(response);
            }
            // Upload ảnh
            Map uploadAnh = cloudinaryService.upload(file);
            String imageUrl = (String) uploadAnh.get("url");
            nhanVien.setHinhAnh(imageUrl);

            // Validate dữ liệu
            if (ten_nhan_vien == null || ten_nhan_vien.trim().isEmpty()) {
                response.put("message", "Tên nhân viên không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setTen(ten_nhan_vien);

            if (can_cuoc_cong_dan == null || !can_cuoc_cong_dan.matches("\\d{12}")) {
                response.put("message", "Căn cước công dân phải là chuỗi gồm 12 chữ số.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setCanCuocCongDan(can_cuoc_cong_dan);

            if (ngay_sinh == null) {
                response.put("message", "Ngày sinh không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }
            LocalDate now = LocalDate.now();
            LocalDate birthDate = ngay_sinh.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (Period.between(birthDate, now).getYears() < 18) {
                response.put("message", "Nhân viên phải trên 18 tuổi.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setNgaySinh(ngay_sinh);
            nhanVien.setGioiTinh(gioi_tinh);

            if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                response.put("message", "Email không đúng định dạng.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setEmail(email);

            if (so_dien_thoai == null || !so_dien_thoai.matches("^0[0-9]{9,10}$")) {
                response.put("message", "Số điện thoại không hợp lệ.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setSoDienThoai(so_dien_thoai.trim());

            if (tinh_thanh_tho == null || tinh_thanh_tho.trim().isEmpty()) {
                response.put("message", "Tỉnh/Thành phố không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setTinhThanhPho(tinh_thanh_tho);

            if (quan_huyen == null || quan_huyen.trim().isEmpty()) {
                response.put("message", "Quận/Huyện không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setQuanHuyen(quan_huyen);

            if (xa_phuong == null || xa_phuong.trim().isEmpty()) {
                response.put("message", "Phường/Xã không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setXaPhuong(xa_phuong);

            if (so_nha_ngo_duong == null || so_nha_ngo_duong.trim().isEmpty()) {
                response.put("message", "Số nhà/ngõ/đường không được để trống.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setSoNhaNgoDuong(so_nha_ngo_duong);
            if (mat_khau == null || mat_khau.trim().isEmpty() || mat_khau.length() < 6) {
                response.put("message", "Mật khẩu không được để trống và phải lớn hơn 6 ký tự.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setMatKhau(mat_khau);
            ChucVu chucVu = chucVuRepository.findById(chuc_vu_id).orElse(null);
            if (chucVu == null) {
                response.put("message", "Vui lòng chọn 1 chức vụ.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setMa(nhanVienService.taoMaTuDong());
            nhanVien.setChucVu(chucVu);
            nhanVien.setNgayTao(new Date());
            nhanVien.setTrangThai(true);
            NhanVien nv = nhanVienRepository.profileNhanVien();
            if (nv == null) {
                response.put("message", "Không tìm thấy thông tin nhân viên cập nhật.");
                return ResponseEntity.badRequest().body(response);
            }
            nhanVien.setNguoiTao(nv.getTen());
            nhanVienRepository.save(nhanVien);

            response.put("message", "Nhân viên mới đã được thêm thành công.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Đã xảy ra lỗi trong quá trình thêm nhân viên.");
            return ResponseEntity.badRequest().body(response);
        }
    }
    @RestController
    @RequestMapping("/api")
    public class NhanVienAPIController {

        @Autowired
        private NhanVienRepository nhanVienRepository;

        @PostMapping("/check-email")
        public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody Map<String, String> request) {
            String email = request.get("email");
            boolean exists = nhanVienRepository.findByEmail(email) != null;
            return ResponseEntity.ok(Collections.singletonMap("exists", exists));
        }
    }


    @GetMapping("/view-update/{id}")
    public String formSuaNhanVien(@PathVariable("id") Integer id, Model model) {
        NhanVien nhanVien = nhanVienRepository.findById(id).orElse(null);
        List<ChucVu> chucVuList = chucVuRepository.findAll();
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("chucVuList", chucVuList);
        model.addAttribute("nowday", LocalDate.now());
        return "admin/nhan-vien/sua";
    }
    @PostMapping("/update")
    public String suaNhanVien(NhanVien nhanVien) {
        nhanVien.setNgaySua(new Date());

        // Kiểm tra và thiết lập các giá trị mặc định nếu cần
        if (nhanVien.getGioiTinh() == null) {
            nhanVien.setGioiTinh(false); // Hoặc một giá trị mặc định khác
        }

        nhanVienRepository.save(nhanVien);
        return "redirect:/admin/nhan-vien/index";
    }

    @GetMapping("/thong-tin-tai-khoan")
    public String thongTinNhanVien(Model model){
        NhanVien nv = nhanVienRepository.profileNhanVien();
        model.addAttribute("nhanVienInfo",nv);
        return "admin/fragments/header";
    }

}
