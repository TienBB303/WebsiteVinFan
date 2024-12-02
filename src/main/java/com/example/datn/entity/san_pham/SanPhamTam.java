package com.example.datn.entity.san_pham;

import com.example.datn.entity.thuoc_tinh.KieuQuat;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamTam {
    Long id; // ID tạm thời, không cần thiết trong cơ sở dữ liệu

    String ma;

    String ten;

    String mo_ta;

    Date ngay_tao;

    Date ngay_sua;

    Boolean trang_thai;

    KieuQuat kieuQuat;
}
