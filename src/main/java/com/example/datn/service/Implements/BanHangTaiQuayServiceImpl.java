package com.example.datn.service.Implements;

import com.example.datn.dto.request.SPCTRequest;
import com.example.datn.dto.response.HinhThucThanhToanResponse;
import com.example.datn.entity.*;
import com.example.datn.repository.*;
import com.example.datn.service.BanHangTaiQuay.BanHangTaiQuayService;
import com.example.datn.service.HoaDonService;
import com.example.datn.service.TrangThaiHoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BanHangTaiQuayServiceImpl implements BanHangTaiQuayService {
    private final HoaDonService hoaDonService;
    private final TrangThaiHoaDonService trangThaiHoaDonService;
    private final HoaDonRepo hoaDonRepo;
    private final LichSuHoaDonRepo lichSuHoaDonRepo;
    private final SPCTRepo spctRepo;
    private final KhachHangRepo khachHangRepo;
    private final NhanVienRepository nhanVienRepository;
    Long myHoaDon = null;


    @Override
    @Transactional
    public void taoHoaDonCho(HoaDon hoaDon) {
        List<HoaDon> listHoaDon = hoaDonService.getAll();
        int count = (int) listHoaDon.stream()
                .filter(sl -> sl.getTrangThai() == trangThaiHoaDonService.getTrangThaiHoaDonRequest().getHoaDonCho())
                .count();
        if (count >= 10) {
            // Thông báo khi số lượng hóa đơn chờ vượt quá 10
            throw new IllegalStateException("Số lượng hóa đơn chờ tối qua là 10");
        }
        //tao hoa don
        NhanVien nhanVien = nhanVienRepository.profileNhanVien();
        hoaDon.setNhanVien(nhanVien);

        hoaDon.setMa(hoaDonService.generateOrderCode());
        hoaDon.setTrangThai(trangThaiHoaDonService.getTrangThaiHoaDonRequest().getHoaDonCho());
        hoaDon.setNgayTao(LocalDate.now());
        hoaDon.setLoaiHoaDon(true);
        // Lấy khách hàng và thêm tên vào hoaDon
        KhachHang khachHang = getKhachHangLe(1L);
        hoaDon.setKhachHang(khachHang);
        hoaDon.setTenNguoiNhan(khachHang.getTen());
        HinhThucThanhToanResponse hinhThucThanhToanResponse = hoaDonService.getHinhThucThanhToan();
        hoaDon.setHinhThucThanhToan(hinhThucThanhToanResponse.getTienMat());
        hoaDon.setNguoiTao(nhanVien.getTen());

        hoaDonRepo.saveAndFlush(hoaDon);

        //tao lich su hoa don
        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setHoaDon(hoaDon);
        lichSuHoaDon.setNgayTao(hoaDon.getNgayTao());
        lichSuHoaDon.setTrangThai(hoaDon.getTrangThai());
        lichSuHoaDon.setNguoiTao(nhanVien.getTen());

        lichSuHoaDonRepo.save(lichSuHoaDon);
    }

    @Override
    public List<HoaDon> findHoaDon() {
        return hoaDonRepo.findAll().stream().
                filter(loc -> loc.getTrangThai() == trangThaiHoaDonService.getTrangThaiHoaDonRequest()
                        .getHoaDonCho()).toList();
    }

    @Override
    public Long getIdHoaDon() {
        return myHoaDon;
    }

    @Override
    public BigDecimal getTongTien(Long idHD) {
        HoaDon hoaDon = hoaDonRepo.findById(idHD).orElse(null);
        return hoaDon != null ? hoaDon.getTongTien() : BigDecimal.ZERO;
    }

    @Override
    public KhachHang getKhachHangLe(Long id) {
        return khachHangRepo.findById(id).orElseThrow(() -> new RuntimeException("Customer not found with id " + id));
    }


}
