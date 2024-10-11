package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "thanh_toan")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thanh_toan")
    Integer id;
    @Column(name = "ma")
    String ma;
    @Column(name = "hinh_thuc_thanh_toan")
    String hinhThucThanhToan;
    @Column(name = "tong_tien")
    BigDecimal tongTien;
    @Column(name = "ngay_tao")
    LocalDate ngayTao;
    @Column(name = "ngay_sua")
    LocalDate ngaySua;
    @Column(name = "nguoi_tao")
    String nguoiTao;
    @Column(name = "nguoi_sua")
    String nguoiSua;
    @Column(name = "trang_thai")
    Integer trangThai;
//    @ManyToMany(mappedBy = "thanhToan")
//     Set<HoaDon> hoaDon;
}
