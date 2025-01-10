package com.example.datn.repository;

import com.example.datn.entity.SanPham;
import com.example.datn.entity.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SPCTRepo extends JpaRepository<SanPhamChiTiet, Long> {

//  nhận các giá trị khi phân trang và tìm kiếm
    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN spct.sanPham sp " +
            "WHERE (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.ma) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.mo_ta) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:trang_thai IS NULL OR spct.trang_thai = :trang_thai) " +
            "AND spct.gia BETWEEN :minPrice AND :maxPrice")
    Page<SanPhamChiTiet> searchProducts(String query, BigDecimal minPrice, BigDecimal maxPrice, Boolean trang_thai, Pageable pageable);

    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN spct.sanPham sp " +
            "WHERE (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.ma) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.mo_ta) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND spct.gia BETWEEN :minPrice AND :maxPrice")
    Page<SanPhamChiTiet> searchProductsWithouttrangThai(String query, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
//  Tìm sản phẩm cso giá cao nhất hiện tại
    @Query("SELECT MAX(spct.gia) FROM SanPhamChiTiet spct")
    BigDecimal findMaxPrice();

    @Query("SELECT MIN(spct.gia) FROM SanPhamChiTiet spct")
    BigDecimal findMinPrice();

    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham.id = :sanPhamId")
    List<SanPhamChiTiet> findBySanPhamId(@Param("sanPhamId") Long sanPhamId);

    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.id NOT IN :ids AND spct.trang_thai = true")
    List<SanPhamChiTiet> findByIdNotIn(@Param("ids") List<Long> ids);

    @Query("SELECT sp FROM SanPhamChiTiet sp WHERE sp.trang_thai = true AND sp.sanPham.ten LIKE %:ten%")
    List<SanPhamChiTiet> timKiemTheoTen(@Param("ten") String ten);

    @Query("SELECT sp FROM SanPhamChiTiet sp WHERE sp.sanPham.ten LIKE %:ten%")
    SanPhamChiTiet timKiem1SPCTTheoTenSP(String ten);

    //qanh
    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN spct.sanPham sp " +
            "WHERE (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(spct.mauSac.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(spct.congSuat.ten) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<SanPhamChiTiet> searchSPCTInHDCT(String query);

    //khoi
    @Query("SELECT sp FROM SanPhamChiTiet sp JOIN sp.sanPham s WHERE sp.id = :productId")
    Optional<SanPhamChiTiet> findChiTietById(@Param("productId") Long productId);

    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham.ma = :maSanPham AND spct.trang_thai = true")
    List<SanPhamChiTiet> findBySanPhamMa(@Param("maSanPham") String maSanPham);

    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN spct.sanPham sp WHERE sp.ma = :maSanPham AND spct.trang_thai = true")
    List<SanPhamChiTiet> findAllBySanPhamMa(@Param("maSanPham") String maSanPham);

    @Query("SELECT spct FROM SanPhamChiTiet spct WHERE spct.sanPham.kieuQuat.id = :kieuQuatId AND spct.trang_thai = true")
    List<SanPhamChiTiet> findBySanPhamKieuQuat(@Param("kieuQuatId") Integer kieuQuatId);

    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN spct.sanPham sp " +
            "WHERE (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.ma) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.mo_ta) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:trang_thai IS NULL OR spct.trang_thai = :trang_thai) " +
            "AND spct.gia BETWEEN :minPrice AND :maxPrice")
    List<SanPhamChiTiet> xuatExcel(String query, BigDecimal minPrice, BigDecimal maxPrice, Boolean trang_thai);

    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN spct.sanPham sp " +
            "WHERE (LOWER(sp.ten) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.ma) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(sp.mo_ta) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND spct.gia BETWEEN :minPrice AND :maxPrice")
    List<SanPhamChiTiet> xuatExcelWithouttrangThai(String query, BigDecimal minPrice, BigDecimal maxPrice);
}
