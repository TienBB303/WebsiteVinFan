package com.example.vinfan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class HoaDonChiTietDTO {
    private String maHoaDon;
    private String tenSanPham;
    private String hinhAnh;
    private Integer soLuong;
    private BigDecimal gia;
    private BigDecimal thanhTien;
}
