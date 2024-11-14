package com.example.datn.repository;

import com.example.datn.dto.response.ListSanPhamInHoaDonChiTietResponse;
import com.example.datn.dto.response.ListSpNewInHoaDonResponse;
import com.example.datn.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
            "spct.id,sp.ten, spct.so_luong, spct.gia) " +
            "FROM SanPhamChiTiet spct " +
            "JOIN spct.sanPham sp ")
    List<ListSpNewInHoaDonResponse> findSanPhamInHoaDon();
}
