package com.example.datn.repository;

import com.example.datn.dto.response.ListSanPhamInHoaDonChiTietResponse;
import com.example.datn.dto.response.ListSpNewInHoaDonResponse;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonChiTietRepo extends JpaRepository<HoaDonChiTiet,Long> {
    @Query("SELECT new com.example.datn.dto.response.ListSanPhamInHoaDonChiTietResponse(" +
            "hd.ma, sp.ten, hdct.soLuong, hdct.gia, hdct.thanhTien) " +
            "FROM HoaDonChiTiet hdct " +
            "JOIN hdct.hoaDon hd " +
            "JOIN hdct.sanPhamChiTiet spct " +
            "JOIN spct.sanPham sp " +
            "WHERE hd.id = :hoaDonId")
    List<ListSanPhamInHoaDonChiTietResponse> findSanPhamByHoaDonId(@Param("hoaDonId") Long hoaDonId);


    @Query("SELECT new com.example.datn.dto.response.ListSpNewInHoaDonResponse(" +
            "sp.ten, spct.so_luong, spct.gia) " +
            "FROM SanPhamChiTiet spct " +
            "JOIN spct.sanPham sp ")
    List<ListSpNewInHoaDonResponse> findSanPhamInHoaDon();

    @Query("SELECT spct FROM SanPhamChiTiet spct JOIN spct.sanPham sp WHERE spct.sanPham.id = sp.id")
    List<SanPhamChiTiet> findSanPhamChiTietWithSanPham();

    // Tìm chi tiết hóa đơn theo hóa đơn và sản phẩm chi tiết
    Optional<HoaDonChiTiet> findByHoaDonAndSanPhamChiTiet(HoaDon hoaDon, SanPhamChiTiet sanPhamChiTiet);

    List<HoaDonChiTiet> findByHoaDon_Id(Long idHD);

    HoaDonChiTiet findByHoaDon_IdAndSanPhamChiTiet_Id(Long idHoaDon, Long idSanPhamChiTiet);


    @Modifying
    @Transactional
    @Query("DELETE FROM HoaDonChiTiet h WHERE h.sanPhamChiTiet.id = :idSanPhamChiTiet")
    void deleteSanPhamChiTiet_Id(@Param("idSanPhamChiTiet") Long idSanPhamChiTiet);

    @Modifying
    @Transactional
    @Query("UPDATE HoaDonChiTiet h SET h.soLuong = :soLuong WHERE h.id = :idSanPhamChiTiet")
    int updateSoLuong(@Param("soLuong") int soLuong, @Param("idSanPhamChiTiet") Long idSanPhamChiTiet);

    @Query("SELECT SUM(hdct.soLuong) FROM HoaDonChiTiet hdct WHERE hdct.sanPhamChiTiet.id = :sanPhamChiTietId")
    Integer getTotalSoLuongBySanPhamChiTiet(@Param("sanPhamChiTietId") Long sanPhamChiTietId);

    List<HoaDonChiTiet> findByHoaDonId(Long hoaDonId);
//    @Modifying
//    @Query("UPDATE HoaDonChiTiet hdc SET hdc.soLuong = :soLuong WHERE hdc.id = :id")
//    void updateSoLuong(@Param("soLuong") Integer soLuong, @Param("id") Long id);

}
