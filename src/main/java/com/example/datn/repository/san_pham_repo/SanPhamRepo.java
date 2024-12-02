package com.example.datn.repository.san_pham_repo;

import com.example.datn.entity.san_pham.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SanPhamRepo extends JpaRepository<SanPham, Long> {
    @Query("SELECT MAX(sp.ma) FROM SanPham sp")
    String findMaxCode();

    @Query("SELECT sp.ten FROM SanPham sp")
    List<String> getTenTonTai();
}
