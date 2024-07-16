package com.example.vinfan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonDTO {
    private Integer id;
    private String tenKhachHang;
    private String maNhanVien;
    private String hinhThucThanhToan;
    private String tenPhieuGiamGia;
    private String ma;
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiamGia;
    private String diaChi;
    private BigDecimal phiVanChuyen;
    private String ghiChu;
    private boolean loaiHoaDon;
    private LocalDate ngayTao;
    private LocalDate ngaySua;
    private String nguoiTao;
    private String nguoiSua;
    private Integer trangThai;

}
