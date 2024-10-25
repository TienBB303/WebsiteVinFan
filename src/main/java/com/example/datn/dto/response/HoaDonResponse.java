package com.example.datn.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HoaDonResponse {
    String maPGG;
    String tenPGG;
    BigDecimal tongTien;
    BigDecimal tongTienSauGiamGia;
    BigDecimal phiVanChuyen;
}
