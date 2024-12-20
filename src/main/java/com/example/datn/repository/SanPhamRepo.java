package com.example.datn.repository;

import com.example.datn.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SanPhamRepo extends JpaRepository<SanPham, Long> {
    @Query("SELECT MAX(sp.ma) FROM SanPham sp WHERE sp.ma LIKE 'SP%'")
    String findMaxCode();

    @Query("SELECT sp.ten FROM SanPham sp")
    List<SanPham> getTenTonTai(String ten);

    @Query("SELECT sp FROM SanPham sp " +
            "WHERE LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.ma) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<SanPham> findByTenOrMa(String query, Pageable pageable);

    SanPham findByMa(String ma);

    @Query("SELECT sp FROM SanPham sp " +
            "WHERE LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<SanPham> findByTenIgnoreCase(String query);
}
