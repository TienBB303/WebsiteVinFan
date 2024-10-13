package com.example.datn.entity;

import com.example.datn.entity.thuoc_tinh.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamChiTietTam {
    Long id; // ID tạm thời, không cần thiết trong cơ sở dữ liệu
    private MauSac mauSac;
    private ChatLieuCanh chatLieuCanh;
    private NutBam nutBam;
    private DuongKinhCanh duongKinhCanh;
    private ChatLieuKhung chatLieuKhung;
    private CongSuat congSuat;
    private DieuKhienTuXa dieuKhienTuXa;
    private Hang hang;
    private ChieuCao chieuCao;
    private DeQuat deQuat;
    private CheDoGio cheDoGio;
    private HinhAnh hinhAnh;

    private BigDecimal gia;
    private Integer so_luong;
    private Date ngay_tao;
    private String nguoi_tao;
    private Date ngay_sua;
    private String nguoi_sua;

    private Boolean trang_thai;
    private SanPhamTam sanPhamTam;
}
