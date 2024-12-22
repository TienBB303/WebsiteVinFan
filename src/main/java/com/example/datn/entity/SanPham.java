package com.example.datn.entity;

import com.example.datn.entity.thuoc_tinh.KieuQuat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "san_pham")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String ma;

    String ten;

    String mo_ta;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date ngay_tao;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date ngay_sua;

    Boolean dieu_khien_tu_xa;

    Boolean trang_thai;

    @ManyToOne
    @JoinColumn(name = "id_kieu_quat")
    KieuQuat kieuQuat;

}
