package com.example.datn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "dia_chi")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaChi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang", nullable = false)
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    private String tinhThanhPho;
    private String quanHuyen;
    private String xaPhuong;
    private String soNhaNgoDuong;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngayTao;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySua;
    private String nguoiTao;
    private String nguoiSua;
    private Boolean trangThai;
}
