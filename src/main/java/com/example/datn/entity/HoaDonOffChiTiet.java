package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "hoa_don_off_chi_tiet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonOffChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don_off")
    private HoaDonOff hoaDonOff;

    @ManyToOne
    @JoinColumn(name = "id_san_pham_chi_tiet")
    private SanPhamChiTiet sanPhamChiTiet;

    private Integer so_luong = 0; // Khởi tạo giá trị mặc định
    private BigDecimal gia_ban = BigDecimal.ZERO;
    private BigDecimal thanh_tien = BigDecimal.ZERO;

    public HoaDonOffChiTiet(HoaDonOff hoaDonOff, SanPhamChiTiet sanPhamChiTiet) {
        this.hoaDonOff = hoaDonOff;
        this.sanPhamChiTiet = sanPhamChiTiet;
        this.so_luong = 0;
        this.gia_ban = BigDecimal.ZERO;
        this.thanh_tien = BigDecimal.ZERO;
    }

    public HoaDonOffChiTiet(HoaDonOff hoaDonOff, SanPhamChiTiet sanPhamChiTiet, int soLuong) {
    }
}
