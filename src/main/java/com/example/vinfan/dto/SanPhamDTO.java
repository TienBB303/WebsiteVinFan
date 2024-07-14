package com.example.vinfan.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class SanPhamDTO {
    private String ma;
    private String ten;
    private String moTa;
    private BigDecimal gia;
    private BigDecimal giaNhap;
    private Integer soLuong;
    private Boolean trangThai;
    private Integer kieuQuatId;
    private Integer mauSacId;
    private Integer nutBamId;
    private Integer congSuatId;
    private Integer chatLieuCanhId;
    private Integer duongKinhCanhId;
    private Integer chatLieuKhungId;
    private Integer deQuatId;
    private Integer chieuCaoId;
    private Integer hangId;
    private Integer cheDoGioId;
    private Integer dieuKhienTuXaId;
    private MultipartFile hinhAnhFile;
}
