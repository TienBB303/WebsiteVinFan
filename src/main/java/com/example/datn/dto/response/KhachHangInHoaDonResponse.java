package com.example.datn.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KhachHangInHoaDonResponse {
    String tenNguoiNhan;
    String sdtNguoiNhan;
    String diaChi;
}
