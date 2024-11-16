package com.example.datn.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LichSuThanhToanResponse {
    BigDecimal tongTien;
    LocalDate ngayTao;
    boolean loaiHoaDon;
    String hinhThucThanhToan;
    Integer trangThai;
    String nguoiTao;
}
