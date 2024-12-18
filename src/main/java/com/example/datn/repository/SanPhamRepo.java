package com.example.datn.repository;

import com.example.datn.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SanPhamRepo extends JpaRepository<SanPham, Long> {
    @Query("SELECT MAX(sp.ma) FROM SanPham sp")
    String findMaxCode();

    @Query("SELECT sp.ten FROM SanPham sp")
    List<String> getTenTonTai();

    List<SanPham> findByMa(String ma); // Lấy sản phẩm

}
