package com.example.datn.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddSPToHoaDonChiTietRequest {
    Long idSP;
    Long idHD;
    String ten;
    BigDecimal gia;
    Integer soLuong;
}
