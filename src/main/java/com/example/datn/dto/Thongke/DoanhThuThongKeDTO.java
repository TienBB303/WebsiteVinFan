package com.example.datn.dto.Thongke;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoanhThuThongKeDTO {
    private String tenSanPham;     // Tên sản phẩm (nếu thống kê theo sản phẩm)
    private Long hoaDonId;         // ID hóa đơn (nếu thống kê theo hóa đơn)
    private BigDecimal tongDoanhThu;  // Sử dụng BigDecimal thay vì Double
    private Long soLuong;          // Số lượng sản phẩm đã bán
    private LocalDate ngay;        // Ngày (nếu thống kê theo ngày)

    public DoanhThuThongKeDTO(LocalDate ngay, BigDecimal tongDoanhThu) {
    }

    public DoanhThuThongKeDTO(Long hoaDonId, BigDecimal tongDoanhThu) {
    }
}
