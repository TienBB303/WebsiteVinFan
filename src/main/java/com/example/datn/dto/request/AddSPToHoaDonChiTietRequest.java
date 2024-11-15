package com.example.datn.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddSPToHoaDonChiTietRequest {
    Long id;
    String ten;
    BigDecimal gia;
    Integer soLuong;
}
