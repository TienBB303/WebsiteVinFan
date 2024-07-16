package com.example.vinfan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "thanh_toan")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thanh_toan")
    private Integer id;
    @Column(name = "ma")
    private String ma;
    @Column(name = "hinh_thuc_thanh_toan")
    private String hinhThucThanhToan;
    @Column(name = "tong_tien")
    private BigDecimal tongTien;
    private LocalDate ngayTao;
    @Column(name = "ngay_sua")
    private LocalDate ngaySua;
    @Column(name = "nguoi_tao")
    private String nguoiTao;
    @Column(name = "nguoi_sua")
    private String nguoiSua;
    @Column(name = "trang_thai")
    private Integer trangThai;
    @ManyToMany(mappedBy = "thanhToan")
    private Set<HoaDon> hoaDon;
}
