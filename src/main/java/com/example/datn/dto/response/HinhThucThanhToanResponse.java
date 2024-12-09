package com.example.datn.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HinhThucThanhToanResponse {
    String thanhToanKhiNhanHang;
    String chuyenKhoan;
    String tienMat;
}
