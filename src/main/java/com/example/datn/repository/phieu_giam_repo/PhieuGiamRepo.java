package com.example.datn.repository.phieu_giam_repo;

import com.example.datn.entity.phieu_giam.PhieuGiam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuGiamRepo extends JpaRepository<PhieuGiam, Integer> {
    @Query("SELECT MAX(pgg.ma) FROM PhieuGiam pgg")
    String findMaxCode();

    Page<PhieuGiam> findByTenLikeAndTrangThai(String ten, boolean trangThai, Pageable pageable);

    Page<PhieuGiam> findByTenLike(String ten, Pageable pageable);

    @Query("SELECT pg FROM PhieuGiam pg JOIN pg.spct spct WHERE spct.id = :sanPhamChiTietId")
    Optional<PhieuGiam> findBySanPhamChiTietId(@Param("sanPhamChiTietId") Long sanPhamChiTietId);

    @Query("SELECT p.spct.id FROM PhieuGiam p")
    List<Long> findAllSanPhamChiTietIds();
    @Query("SELECT pg FROM PhieuGiam pg WHERE pg.spct IS NOT NULL")
    List<PhieuGiam> findAllWithLinkedSpct(); // Lấy tất cả ID của SanPhamChiTiet có trong bảng phieu_giam_san_pham

    @Query("SELECT pgs FROM PhieuGiam pgs " +
            "JOIN pgs.spct spct " +
            "JOIN spct.sanPham sp " +
            "WHERE LOWER(sp.ten) LIKE LOWER(CONCAT('%', :ten, '%'))")
    List<PhieuGiam> timKiemSanPhamCoGiamGia(@Param("ten") String ten);

    @Query("SELECT pgs FROM PhieuGiam pgs " +
            "JOIN pgs.spct spct " +
            "JOIN spct.sanPham sp " +
            "WHERE sp.ma = :maSanPham")
    List<PhieuGiam> findBySanPhamMa(@Param("maSanPham") String maSanPham);

    Optional<PhieuGiam> findByMaAndLoaiPhieuGiam(String ma, boolean loaiPhieuGiam);

    List<PhieuGiam> findByLoaiPhieuGiam(boolean loaiPhieuGiam);


}
