package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "hoa_don_off")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonOff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "id_hinh_thuc_thanh_toan")
    private HinhThucThanhToan hinhThucThanhToan;

    @ManyToOne
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiam phieuGiam;

    private String ma;
    private BigDecimal tong_tien;
    private BigDecimal tong_tien_sau_giam_gia;
    private String ghi_chu;
}
