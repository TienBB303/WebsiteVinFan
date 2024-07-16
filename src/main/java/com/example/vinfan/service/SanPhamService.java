package com.example.vinfan.service;

import com.example.vinfan.entity.SanPham;
import com.example.vinfan.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface SanPhamService {

    Page<SanPhamChiTiet> findAll(Pageable pageable);

    void create(SanPham sanPham, SanPhamChiTiet sanPhamChiTiet);

    SanPhamChiTiet findById(Long id);

    Boolean update(SanPhamChiTiet sanPhamChiTiet);

    Boolean delete(Long id);

    String taoMaTuDong();

    Page<SanPhamChiTiet> searchProducts(String query, BigDecimal minPrice, BigDecimal maxPrice, Boolean trangThai, Pageable pageable);
}
