package com.example.datn.repository;

import com.example.datn.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface SPCTRepo extends JpaRepository<SanPhamChiTiet, Long> {

//  nhận các giá trị khi phân trang và tìm kiếm
    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN spct.sanPham sp " +
            "WHERE (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.ma) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.mo_ta) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND spct.gia BETWEEN :minPrice AND :maxPrice")
    Page<SanPhamChiTiet> searchProducts(String query, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

//  Tìm sản phẩm cso giá cao nhất hiện tại
    @Query("SELECT MAX(spct.gia) FROM SanPhamChiTiet spct")
    BigDecimal findMaxPrice();
}
