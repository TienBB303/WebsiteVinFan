package com.example.datn.dto.Thongke;

import com.example.datn.entity.SanPhamChiTiet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamBanChayDTO {
    private String tenSanPham;
    private Long soLuong;
}
