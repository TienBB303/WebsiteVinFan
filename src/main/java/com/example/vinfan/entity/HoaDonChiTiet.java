package com.example.vinfan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "hoa_don_chi_tiet")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class HoaDonChiTiet {
//    CREATE TABLE hoa_don_chi_tiet (
//    id BIGINT PRIMARY KEY IDENTITY(1, 1),
//    id_hoa_don BIGINT FOREIGN KEY REFERENCES hoa_don(id),
//    id_san_pham_chi_tiet BIGINT FOREIGN KEY REFERENCES san_pham_chi_tiet(id),
//    so_luong INT,
//    gia_ban MONEY,
//    thanh_tien MONEY,
//    trang_thai INT
//);
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(
            name = "id_san_pham_chi_tiet"
    )
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "so_luong")
    private Integer soLuong;
    @Column(name = "gia_ban")
    private BigDecimal gia;
    @Column(name = "thanh_tien")
    private BigDecimal thanhTien;
    @Column(name = "trang_thai")
    private Integer trangThai;

}
