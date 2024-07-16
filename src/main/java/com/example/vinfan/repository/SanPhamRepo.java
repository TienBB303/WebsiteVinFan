package com.example.vinfan.repository;

import com.example.vinfan.entity.SanPham;
import com.example.vinfan.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface SanPhamRepo extends JpaRepository<SanPham, Long> {
    @Query("SELECT MAX(sp.ma) FROM SanPham sp")
    String findMaxCode();


    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN spct.sanPham sp " +
            "WHERE (sp.ten LIKE %:query% OR sp.ma LIKE %:query% OR sp.mo_ta LIKE %:query%) " +
            "AND (spct.gia BETWEEN :minPrice AND :maxPrice)" +
            "AND sp.trang_thai = :trangThai")
    Page<SanPhamChiTiet> searchProducts(String query, BigDecimal minPrice, BigDecimal maxPrice, Boolean trangThai, Pageable pageable);


}
