package com.example.datn.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrangThaiHoaDonResponse {
    Integer tatCa;
    Integer choXacNhan;
    Integer daXacNhan;
    Integer dangGiaoHang;
    Integer daGiaoHang;
    Integer huy;
}