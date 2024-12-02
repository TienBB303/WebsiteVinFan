package com.example.datn.service;

import com.example.datn.entity.HoaDonOff;
import com.example.datn.entity.HoaDonOffChiTiet;
import com.example.datn.entity.san_pham.SanPhamChiTiet;
import com.example.datn.repository.BanOffRepo.HoaDonOffChiTietRepo;
import com.example.datn.repository.BanOffRepo.HoaDonOffRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BanHangOffService {
    @Autowired
    private HoaDonOffRepo hoaDonOffRepo;

    @Autowired
    private HoaDonOffChiTietRepo hoaDonOffChiTietRepo;

    @Autowired
    private SanPhamService sanPhamService;

    public HoaDonOff taoHoaDon(){
        HoaDonOff hoaDonOff = new HoaDonOff();
        hoaDonOffRepo.save(hoaDonOff);
        return hoaDonOff;
    }

    public List<HoaDonOff> findAllHoaDon(){
        return hoaDonOffRepo.findAll();
    }

    public Optional<HoaDonOff> findOnceHoaDon(Long hoaDonOffId){
        return hoaDonOffRepo.findById(hoaDonOffId);
    }

    public void luuHoaDonOffChiTiet(HoaDonOffChiTiet hoaDonOffChiTiet){
        hoaDonOffChiTietRepo.save(hoaDonOffChiTiet);
    }

    public HoaDonOffChiTiet checkTonTaiSanPhamTrongGio(HoaDonOff hoaDonOff, SanPhamChiTiet sanPhamChiTiet) {
        return hoaDonOffChiTietRepo.findByHoaDonOffAndSanPhamChiTiet(hoaDonOff, sanPhamChiTiet)
                .orElse(null);
    }

    public List<HoaDonOffChiTiet> getChiTietByHoaDonId(Long hoaDonOffId) {
        if (hoaDonOffId == null) {
            return List.of(); // Trả về danh sách rỗng nếu ID hóa đơn không được cung cấp
        }
        return hoaDonOffChiTietRepo.findAllById(hoaDonOffId);
    }

    public BigDecimal getTongTien(Long hoaDonOffId) {
        if (hoaDonOffId == null) {
            return BigDecimal.ZERO; // Trả về 0 nếu ID hóa đơn không được cung cấp
        }
        List<HoaDonOffChiTiet> chiTietList = hoaDonOffChiTietRepo.findAllById(hoaDonOffId);
        return chiTietList.stream()
                .map(HoaDonOffChiTiet::getThanh_tien)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Tính tổng tiền
    }

    public void themSanPhamVaoGio(Long hoaDonOffId, Long sanPhamId, int soLuong) {
        // Lấy thông tin hóa đơn
        HoaDonOff hoaDonOff = hoaDonOffRepo.findById(hoaDonOffId).orElseThrow();

        // Lấy thông tin sản phẩm chi tiết
        SanPhamChiTiet sanPhamChiTiet = sanPhamService.findById(sanPhamId);

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        HoaDonOffChiTiet hoaDonOffChiTiet = hoaDonOffChiTietRepo
                .findByHoaDonOffAndSanPhamChiTiet(hoaDonOff, sanPhamChiTiet)
                .orElse(null);

        if (hoaDonOffChiTiet != null) {
            // Nếu sản phẩm đã tồn tại trong giỏ, cập nhật số lượng và thành tiền
            hoaDonOffChiTiet.setSo_luong(hoaDonOffChiTiet.getSo_luong() + soLuong);
            hoaDonOffChiTiet.setThanh_tien(sanPhamChiTiet.getGia().multiply(BigDecimal.valueOf(hoaDonOffChiTiet.getSo_luong())));

            // Lưu lại vào cơ sở dữ liệu
            hoaDonOffChiTietRepo.save(hoaDonOffChiTiet);
        } else {
            // Nếu sản phẩm chưa có trong giỏ, tạo mới chi tiết hóa đơn
            hoaDonOffChiTiet = new HoaDonOffChiTiet(hoaDonOff, sanPhamChiTiet, soLuong);
            hoaDonOffChiTiet.setThanh_tien(sanPhamChiTiet.getGia().multiply(BigDecimal.valueOf(soLuong)));

            // Lưu lại vào cơ sở dữ liệu
            hoaDonOffChiTietRepo.save(hoaDonOffChiTiet);
        }
    }

    public void thanhToanHoaDon(Long hoaDonOffId) {
        HoaDonOff hoaDonOff = hoaDonOffRepo.findById(hoaDonOffId).orElseThrow();
        hoaDonOffRepo.save(hoaDonOff);
    }
}


