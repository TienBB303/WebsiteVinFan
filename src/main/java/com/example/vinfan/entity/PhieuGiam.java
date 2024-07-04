package com.example.vinfan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDate ngayBD;
    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKT;
    @Column(name = "so_luong")
    private int soLuong;
    @Column(name = "loai_phieu_giam_gia")
    private boolean loaiPhieuGiam;
    @Column(name = "gia_tri_toi_thieu_ap_dung")
    private BigDecimal giaTriMin;
    @Column(name = "gia_tri_toi_da_giam")
    private BigDecimal giaTriMax;
    @Column(name = "ngay_tao")
    private LocalDate ngayTao;
    @Column(name = "ngay_sua")
    private LocalDate ngaySua;
    @Column(name = "nguoi_tao")
    private String nguoiTao;
    @Column(name = "nguoi_sua")
    private String nguoiSua;
    @Column(name = "trang_thai")
    private boolean trangThai;

}
