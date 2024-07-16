package com.example.vinfan.entity;

import com.example.vinfan.entity.KhachHangEntity.KhachHang;
import com.example.vinfan.entity.NhanVienEntity.NhanVien;
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
@Table(name = "hoa_don")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HoaDon {
    //    CREATE TABLE hoa_don (
//            id BIGINT PRIMARY KEY IDENTITY(1, 1),
//    id_khach_hang INT FOREIGN KEY REFERENCES khach_hang(id),
//    id_nhan_vien INT FOREIGN KEY REFERENCES nhan_vien(id),
//     INT FOREIGN KEY REFERENCES thanh_toan(id),
//    id_phieu_giam_gia INT FOREIGN KEY REFERENCES phieu_giam_gia(id),
//    ma NVARCHAR(255),
//    ten_nguoi_nhan NVARCHAR(255),
//    sdt_nguoi_nhan NVARCHAR(50),
//    tong_tien MONEY,
//    tong_tien_sau_giam_gia MONEY,
//    dia_chi NVARCHAR(255),
//    phi_van_chuyen MONEY,
//    ghi_chu NVARCHAR(255),
//    loai_hoa_don BIT,
//    ngay_tao DATETIME,
//    ngay_sua DATETIME,
//    nguoi_tao NVARCHAR(255),
//    nguoi_sua NVARCHAR(255),
//    trang_thai INT
//);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToMany
    @JoinTable(
            name = "thanh_toan", // Tên bảng liên kết
            joinColumns = {@JoinColumn(name = "id")}, // Cột liên kết của bảng hiện tại
            inverseJoinColumns = @JoinColumn(name = "id_thanh_toan") // Cột liên kết của bảng ThanhToan
    )
    private Set<ThanhToan> thanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiam phieuGiamGia;

    @Column(name = "ma")
    private String ma;
    @Column(name = "ten_nguoi_nhan")
    private String tenNguoiNhan;
    @Column(name = "sdt_nguoi_nhan")
    private String sdtNguoiNhan;
    @Column(name = "tong_tien")
    private BigDecimal tongTien;
    @Column(name = "tong_tien_sau_giam_gia")
    private BigDecimal tongTienSauGiamGia;
    @Column(name = "dia_chi")
    private String diaChi;
    @Column(name = "phi_van_chuyen")
    private BigDecimal phiVanChuyen;
    @Column(name = "ghi_chu")
    private String ghiChu;
    @Column(name = "loai_hoa_don")
    private boolean loaiHoaDon;
    private LocalDate ngayTao;
    @Column(name = "ngay_sua")
    private LocalDate ngaySua;
    @Column(name = "nguoi_tao")
    private String nguoiTao;
    @Column(name = "nguoi_sua")
    private String nguoiSua;
    @Column(name = "trang_thai")
    private Integer trangThai;


}
