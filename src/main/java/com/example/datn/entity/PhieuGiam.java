package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "phieu_giam_gia")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhieuGiam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma")
    private String ma;

    @Column(name = "ten")
    private String ten;

    @Column(name = "ngay_bat_dau")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngayBD;

    @Column(name = "ngay_ket_thuc")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngayKT;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "loai_phieu_giam_gia")
    private boolean loaiPhieuGiam;

    @Column(name = "gia_tri_toi_thieu_ap_dung")
    private BigDecimal giaTriMin;

    @Column(name = "gia_tri_toi_da_giam")
    private BigDecimal giaTriMax;

    @Column(name = "ngay_tao")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngayTao;

    @Column(name = "ngay_sua")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySua;

    @Column(name = "nguoi_tao")
    private String nguoiTao;

    @Column(name = "nguoi_sua")
    private String nguoiSua;

    @Column(name = "trang_thai")
    private boolean trangThai;

    @ManyToOne
    @JoinColumn(name = "id_san_pham")
    private SanPham sanPham;

    @OneToMany(mappedBy = "phieuGiam")
    private List<SanPham> sanPhams;
}
