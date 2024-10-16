package com.example.datn.repository;

import com.example.datn.entity.PhieuGiam;
import com.example.datn.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuGiamRepo extends JpaRepository<PhieuGiam, Integer> {
    @Query("SELECT MAX(pgg.ma) FROM PhieuGiam pgg")
    String findMaxCode();

    Page<PhieuGiam> findByTenLikeAndTrangThai(String ten, boolean trangThai, Pageable pageable);

    Page<PhieuGiam> findByTenLike(String ten, Pageable pageable);

    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham.phieuGiam IS NOT NULL")
    List<SanPhamChiTiet> findByPhieuGiamNotNull();
}
