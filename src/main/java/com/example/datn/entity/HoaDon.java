package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "hoa_don")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hinh_thuc_thanh_toan")
    HinhThucThanhToan hinhThucThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    PhieuGiam phieuGiamGia;

    @Column(name = "ma")
    String ma;
    @Column(name = "ten_nguoi_nhan")
    String tenNguoiNhan;
    @Column(name = "sdt_nguoi_nhan")
    String sdtNguoiNhan;
    @Column(name = "tong_tien")
    BigDecimal tongTien;
    @Column(name = "tong_tien_sau_giam_gia")
    BigDecimal tongTienSauGiamGia;
    @Column(name = "dia_chi")
    String diaChi;
    @Column(name = "phi_van_chuyen")
    BigDecimal phiVanChuyen;
    @Column(name = "ghi_chu")
    String ghiChu;
    @Column(name = "loai_hoa_don")
    boolean loaiHoaDon;
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

}
