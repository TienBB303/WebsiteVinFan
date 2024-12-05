package com.example.datn.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchSanPhamChiTietRequest {
    private Long idSP;
    private String ten;
    private String mauSac;
    private String congSuat;
    private BigDecimal gia;
    private Integer soLuong;
}
