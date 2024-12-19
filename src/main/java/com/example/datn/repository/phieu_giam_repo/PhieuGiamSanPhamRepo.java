package com.example.datn.repository.phieu_giam_repo;

import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.entity.phieu_giam.PhieuGiamSanPham;
import com.example.datn.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuGiamSanPhamRepo extends JpaRepository<PhieuGiamSanPham, Integer> {
    @Query("SELECT p.sanPhamChiTiet.id FROM PhieuGiamSanPham p")
    List<Long> findAllSanPhamChiTietIds();  // Lấy tất cả ID của SanPhamChiTiet có trong bảng phieu_giam_san_pham

    List<PhieuGiamSanPham> findByPhieuGiamId(Integer phieuGiamId);

    @Query("SELECT pgs.sanPham FROM PhieuGiamSanPham pgs WHERE pgs.phieuGiam.id = :phieuGiamId")
    List<SanPham> findSanPhamByPhieuGiamId(@Param("phieuGiamId") Integer phieuGiamId);

    @Query("SELECT pgs FROM PhieuGiamSanPham pgs " +
            "JOIN pgs.sanPhamChiTiet spct " +
            "JOIN spct.sanPham sp " +
            "WHERE LOWER(sp.ten) LIKE LOWER(CONCAT('%', :ten, '%'))")
    List<PhieuGiamSanPham> timKiemSanPhamCoGiamGia(@Param("ten") String ten);

    Optional<PhieuGiamSanPham> findBySanPhamChiTiet(SanPhamChiTiet sanPhamChiTiet);

    Optional<PhieuGiamSanPham> findBySanPhamChiTietId(Long sanPhamChiTietId);

    @Query("SELECT pgs FROM PhieuGiamSanPham pgs " +
            "JOIN pgs.sanPhamChiTiet spct " +
            "JOIN spct.sanPham sp " +
            "WHERE sp.ma = :maSanPham")
    List<PhieuGiamSanPham> findBySanPhamMa(@Param("maSanPham") String maSanPham);

}
