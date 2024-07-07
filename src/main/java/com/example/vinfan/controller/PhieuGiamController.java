package com.example.vinfan.controller;

import com.example.vinfan.entity.PhieuGiam;
import com.example.vinfan.repository.PhieuGiamRepo;
import com.example.vinfan.service.PhieuGiamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/admin/phieu-giam")
public class PhieuGiamController {

    @Autowired
    PhieuGiamRepo pggRepo;
    @Autowired
    PhieuGiamService pggSV;

    //phieu giam gia
    @GetMapping("/index")
    public String phieuGiam(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(name = "keyword", defaultValue = "") String keyword
    ) {
        if (page < 0) {
            page = 0; // Đảm bảo page không âm
        }
        PageRequest pageable = PageRequest.of(page, size);
        String searchTerm = "%" + keyword + "%";
        Page<PhieuGiam> phieuGiamGiaPage = pggRepo.findByTenLike(searchTerm, pageable);

        model.addAttribute("ListPGG", phieuGiamGiaPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", phieuGiamGiaPage.getTotalPages());

        return "admin/phieu_giam/index"; // Trả về tên view, không cần "/" ở đầu
    }
    @PostMapping("/add")
    public String addPGG(
            Model model,
            @RequestParam("ten") String ten,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam("loaiPhieuGiam") boolean loaiPhieu,
            @RequestParam("giaTriMin") BigDecimal min,
            @RequestParam("giaTriMax") BigDecimal max,
            @RequestParam("nguoiTao") String nguoiTao,
            @RequestParam("trangThai") boolean trangThai
    ) {
        String ma = pggSV.taoMaTuDong();  // Tạo mã sản phẩm bằng tự động

        PhieuGiam sanPham = new PhieuGiam();
        sanPham.setMa(ma);
        sanPham.setTen(ten);
        sanPham.setNgayBD(new Date());  // Set ngày tạo bằng ngày hiện tại
        sanPham.setNgayKT(new Date());
        sanPham.setSoLuong(soLuong);
        sanPham.setLoaiPhieuGiam(loaiPhieu);
        sanPham.setGiaTriMax(max);
        sanPham.setGiaTriMin(min);
        sanPham.setNgayTao(new Date());
        sanPham.setNguoiTao(nguoiTao);
        sanPham.setTrangThai(trangThai);

        pggRepo.save(sanPham);
        return "redirect:/admin/phieu-giam/index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable int id) {
        Optional<PhieuGiam> pggOptional = pggRepo.findById(id);
        if (pggOptional.isPresent()) {
            PhieuGiam pgg = pggOptional.get();
            model.addAttribute("pgg", pgg);
            return "/admin/phieu_giam/edit"; // Template form sửa
        } else {
            // Xử lý khi không tìm thấy phiếu giảm giá với id tương ứng
            return "redirect:/admin/phieu-giam/index";
        }
    }

    @PostMapping("/update/{id}")
    public String updatePGG(
            Model model,
            @PathVariable("id") int id,
            @RequestParam("ten") String ten,
            @RequestParam("ngayBD") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBD,
            @RequestParam("ngayKT") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKT,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam("trangThai") boolean trangThai
    ) {
        Optional<PhieuGiam> optionalPhieuGiam = pggRepo.findById(id);
        if (optionalPhieuGiam.isPresent()) {
            PhieuGiam sanPham = optionalPhieuGiam.get();
            sanPham.setTen(ten);
            sanPham.setNgayBD(ngayBD);
            sanPham.setNgayKT(ngayKT);
            sanPham.setNgaySua(new Date());
            sanPham.setSoLuong(soLuong);
            sanPham.setTrangThai(trangThai);

            pggRepo.save(sanPham); // Lưu lại phiếu giảm giá đã chỉnh sửa
        }

        return "redirect:/admin/phieu-giam/index"; // Điều hướng về trang danh sách phiếu giảm giá
    }


}
