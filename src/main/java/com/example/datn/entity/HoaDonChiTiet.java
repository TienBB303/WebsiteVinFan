package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;

@Entity
@Table(name = "hoa_don_chi_tiet")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HoaDonChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "id_san_pham_chi_tiet")
    SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "so_luong")
    Integer soLuong;

    @Column(name = "gia_ban")
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    BigDecimal gia;

    @Column(name = "gia_sau_giam")
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    BigDecimal giaSauGiam;

    @Column(name = "thanh_tien")
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    BigDecimal thanhTien;

    @Column(name = "trang_thai")
    Integer trangThai;

    @Transient
    BigDecimal giaGiam;
}
