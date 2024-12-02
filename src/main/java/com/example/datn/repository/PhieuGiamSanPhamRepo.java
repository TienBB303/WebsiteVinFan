package com.example.datn.repository;

import com.example.datn.entity.PhieuGiamSanPham;
import com.example.datn.entity.san_pham.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuGiamSanPhamRepo extends JpaRepository<PhieuGiamSanPham, Integer> {
    @Query("SELECT p.sanPhamChiTiet.id FROM PhieuGiamSanPham p")
    List<Long> findAllSanPhamChiTietIds();  // Lấy tất cả ID của SanPhamChiTiet có trong bảng phieu_giam_san_pham

    List<PhieuGiamSanPham> findByPhieuGiamId(Integer phieuGiamId);

    @Query("SELECT pgs.sanPham FROM PhieuGiamSanPham pgs WHERE pgs.phieuGiam.id = :phieuGiamId")
    List<SanPham> findSanPhamByPhieuGiamId(@Param("phieuGiamId") Integer phieuGiamId);
}
