package com.example.datn.repository;

import com.example.datn.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface SPCTRepo extends JpaRepository<SanPhamChiTiet, Long> {

//  nhận các giá trị khi phân trang và tìm kiếm
    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN spct.sanPham sp " +
            "WHERE (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.ma) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.mo_ta) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND spct.gia BETWEEN :minPrice AND :maxPrice")
    Page<SanPhamChiTiet> searchProducts(String query, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

//  Tìm sản phẩm cso giá cao nhất hiện tại
    @Query("SELECT MAX(spct.gia) FROM SanPhamChiTiet spct")
    BigDecimal findMaxPrice();

    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :sanPhamId")
    List<SanPhamChiTiet> findBySanPhamId(@Param("sanPhamId") Long sanPhamId);

    List<SanPhamChiTiet> findByIdNotIn(List<Long> ids);

    @Query("SELECT sp FROM SanPhamChiTiet sp WHERE sp.sanPham.ten LIKE %:ten%")
    List<SanPhamChiTiet> timKiemTheoTen(String ten);

    //qanh
    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN spct.sanPham sp " +
            "WHERE (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(spct.mauSac.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(spct.congSuat.ten) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<SanPhamChiTiet> searchSPCTInHDCT(String query);
}
