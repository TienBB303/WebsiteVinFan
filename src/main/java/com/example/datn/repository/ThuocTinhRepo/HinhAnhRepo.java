package com.example.datn.repository.ThuocTinhRepo;

import com.example.datn.entity.thuoc_tinh.HinhAnh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HinhAnhRepo extends JpaRepository<HinhAnh, Integer> {
    @Query("SELECT h FROM HinhAnh h WHERE h.id IN (SELECT spct.hinhAnh.id FROM SanPhamChiTiet spct WHERE spct.id = :sanPhamChiTietId)")
    List<HinhAnh> findAllBySanPhamChiTietId(@Param("sanPhamChiTietId") Long sanPhamChiTietId);
}
