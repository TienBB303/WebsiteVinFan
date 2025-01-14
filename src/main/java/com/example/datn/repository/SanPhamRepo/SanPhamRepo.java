package com.example.datn.repository.SanPhamRepo;

import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SanPhamRepo extends JpaRepository<SanPham, Long> {
    @Query("SELECT MAX(sp.ma) FROM SanPham sp WHERE sp.ma LIKE 'SP%'")
    String findMaxCode();

    @Query("SELECT sp.ten FROM SanPham sp")
    List<SanPham> getTenTonTai(String ten);

//    @Query("SELECT sp.ma FROM SanPham sp" +
//            " WHERE LOWER(sp.ma) LIKE LOWER(CONCAT('%', :ten, '%'))")
    SanPham findByTen(String ten);

    @Query("SELECT sp FROM SanPham sp " +
            "WHERE LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.ma) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<SanPham> findByTenOrMa(String query, Pageable pageable);

    SanPham findByMa(String ma);

    @Query("SELECT sp FROM SanPham sp " +
            "WHERE LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<SanPham> findByTenIgnoreCase(String query);

    @Query("SELECT sp FROM SanPham sp WHERE LOWER(sp.ma) LIKE LOWER(CONCAT('%', :ma, '%'))")
    Page<SanPham> findByMa(String ma, Pageable pageable);

    @Query("SELECT sp FROM SanPham sp JOIN sp.kieuQuat kq " +
            "WHERE (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.ma) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.mo_ta) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:trang_thai IS NULL OR sp.trang_thai = :trang_thai) " +
            "AND (LOWER(kq.ten) LIKE LOWER(CONCAT('%', :query, '%'))) ")
    Page<SanPham> timSanPham( String query,  Boolean trang_thai, Pageable pageable);

    @Query("SELECT sp FROM SanPham sp JOIN sp.kieuQuat kq " +
            "WHERE (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.ma) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.mo_ta) LIKE LOWER(CONCAT('%', :query, '%'))" +
            "AND (LOWER(kq.ten) LIKE LOWER(CONCAT('%', :query, '%'))) ) ")
    Page<SanPham> timSanPhamKhongTrangThai(String query, Pageable pageable);

    @Query("SELECT SUM(spct.so_luong) FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :idSanPham")
    Long tongSoLuongSanPhamChiTiet(Long idSanPham);

    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :idSanPham")
    Page<SanPhamChiTiet> timSanPhamChiTietTheoSanPham(Long idSanPham, Pageable pageable);

    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :idSanPham")
    List<SanPhamChiTiet> timSanPhamChiTietTheoIDSanPham(Long idSanPham);

//    @Query("SELECT sp FROM SanPham sp WHERE sp.id = :idSPCT")
//    SanPham timSanPhamChiTietTheoSanPham(Long idSPCT);
}
