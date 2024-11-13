package com.example.datn.service;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.HoaDonChiTietRepo;
import com.example.datn.repository.HoaDonRepo;
import com.example.datn.repository.SPCTRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class GioHangOffService {
    @Autowired
    private HoaDonRepo hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepository;

    @Autowired
    private SPCTRepo sanPhamChiTietRepository;

    private Map<Long, Map<Long, Integer>> carts = new HashMap<>();

    //    tạo nhiều hd
    public Long taoHoaDonMoi() {
        HoaDon hoaDon = new HoaDon();
        hoaDon = hoaDonRepository.save(hoaDon);

        carts.put(hoaDon.getId(), new HashMap<>());
        return hoaDon.getId();
    }

    public void checkTonTaiGioHang(Long hoaDonId) {
        if (!carts.containsKey(hoaDonId)) {
            carts.put(hoaDonId, new HashMap<>());
        }
    }

    public void themVaoGio(Long hoaDonId, Long sanPhamChiTietId, int soLuong) {
        checkTonTaiGioHang(hoaDonId);
        Map<Long, Integer> cart = carts.get(hoaDonId);

        cart.put(sanPhamChiTietId, cart.getOrDefault(sanPhamChiTietId, 0) + soLuong);
    }

    public Map<Long, Integer> getCart(Long hoaDonId) {
        return carts.getOrDefault(hoaDonId, new HashMap<>());
    }

    public boolean thanhToan(Long hoaDonId) {
        Map<Long, Integer> cart = getCart(hoaDonId);

        if (cart.isEmpty()) {
            return false;
        }

        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId).orElse(null);
        if (hoaDon == null) return false;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Long sanPhamChiTietId = entry.getKey();
            int soLuong = entry.getValue();

            SanPhamChiTiet sanPham = sanPhamChiTietRepository.findById(sanPhamChiTietId).orElse(null);
            if (sanPham == null || sanPham.getSo_luong() < soLuong) {
                return false;
            }

            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setSanPhamChiTiet(sanPham);
            hoaDonChiTiet.setSoLuong(soLuong);
            hoaDonChiTiet.setGia(sanPham.getGia());
            hoaDonChiTiet.setThanhTien(sanPham.getGia().multiply(BigDecimal.valueOf(soLuong)));
            hoaDonChiTietRepository.save(hoaDonChiTiet);

            sanPham.setSo_luong(sanPham.getSo_luong() - soLuong);
            sanPhamChiTietRepository.save(sanPham);
        }

        carts.remove(hoaDonId);
        return true;
    }
}

