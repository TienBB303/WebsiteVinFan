package com.example.datn.dto.Thongke;

import java.math.BigDecimal;

public class HoaDonDoanhThuDTO {
    private Long hoaDonId;
    private BigDecimal tongDoanhThu;

    public HoaDonDoanhThuDTO(Long hoaDonId, BigDecimal tongDoanhThu) {
        this.hoaDonId = hoaDonId;
        this.tongDoanhThu = tongDoanhThu;
    }

    public Long getHoaDonId() {
        return hoaDonId;
    }

    public void setHoaDonId(Long hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public BigDecimal getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(BigDecimal tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }
}
