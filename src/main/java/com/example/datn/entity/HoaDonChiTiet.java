package com.example.datn.entity;

import com.example.datn.entity.san_pham.SanPhamChiTiet;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    @JoinColumn(
            name = "id_san_pham_chi_tiet"
    )
    SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "so_luong")
     Integer soLuong;
    @Column(name = "gia_ban")
     BigDecimal gia;
    @Column(name = "thanh_tien")
     BigDecimal thanhTien;
    @Column(name = "trang_thai")
     Integer trangThai;
}
