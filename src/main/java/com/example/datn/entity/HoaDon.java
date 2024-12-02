package com.example.datn.entity;

import com.example.datn.entity.phieu_giam.PhieuGiam;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "id_phieu_giam_gia")
    PhieuGiam phieuGiamGia;

    @Column(name = "ma")
    String ma;
    @Column(name = "ten_nguoi_nhan")
    String tenNguoiNhan;
    @Column(name = "sdt_nguoi_nhan")
    String sdtNguoiNhan;
    @Column(name = "tong_tien")
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    BigDecimal tongTien;
    @Column(name = "tong_tien_sau_giam_gia")
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    BigDecimal tongTienSauGiamGia;
    @Column(name = "dia_chi")
    String diaChi;
    @Column(name = "phi_van_chuyen")
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
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

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HoaDonChiTiet> hoaDonChiTietList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hinh_thuc_thanh_toan", nullable = true)
    HinhThucThanhToan hinhThucThanhToan;


}
