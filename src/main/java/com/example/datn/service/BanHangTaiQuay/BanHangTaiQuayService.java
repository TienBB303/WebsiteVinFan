package com.example.datn.service.BanHangTaiQuay;

import com.example.datn.dto.response.HinhThucThanhToanResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.KhachHang;

import java.math.BigDecimal;
import java.util.List;

public interface BanHangTaiQuayService {
    void taoHoaDonCho(HoaDon hoaDon);

    List<HoaDon> findHoaDon();

    Long getIdHoaDon();

    BigDecimal getTongTien(Long idHD);

    KhachHang getKhachHangLe(Long id);


}
