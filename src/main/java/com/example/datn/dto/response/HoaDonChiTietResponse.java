package com.example.datn.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HoaDonChiTietResponse {
    String maHoaDon;
    String tenSanPham;
    Integer soLuong;
    BigDecimal gia;
    BigDecimal thanhTien;
}
