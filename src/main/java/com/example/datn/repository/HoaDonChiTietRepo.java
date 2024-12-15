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

    List<HoaDonChiTiet> findByHoaDon_Id(Long idHoaDon);

    @Modifying
    @Transactional
    @Query("DELETE FROM HoaDonChiTiet h WHERE h.hoaDon.id = :idHoaDon AND h.sanPhamChiTiet.id = :idSanPhamChiTiet")
    void deleteByHoaDon_IdAndSanPhamChiTiet_Id(@Param("idHoaDon") Long idHoaDon,@Param("idSanPhamChiTiet") Long idSanPhamChiTiet);

//    @Modifying
//    @Query("UPDATE HoaDonChiTiet hdc SET hdc.soLuong = :soLuong WHERE hdc.id = :id")
//    void updateSoLuong(@Param("soLuong") Integer soLuong, @Param("id") Long id);

}
