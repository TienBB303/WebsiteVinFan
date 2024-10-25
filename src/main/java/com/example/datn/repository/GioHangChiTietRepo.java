package com.example.datn.repository;

import com.example.datn.entity.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangChiTietRepo extends JpaRepository<GioHangChiTiet,Long> {
    Optional<GioHangChiTiet> findByGioHangIdAndSanPhamChiTietId(Long gioHangId, Long sanPhamChiTietId);
}
