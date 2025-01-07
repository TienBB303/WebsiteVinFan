package com.example.datn.dto.response;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.SanPhamChiTiet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThongKeDTO {
    private LocalDate ngayTao;
    private List<HoaDon> hoaDonsHoanThanh;
    private BigDecimal tongTienThuDuocHomNay;
    private List<SanPhamChiTiet> sanPhamBanChay;
}
