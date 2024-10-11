package com.example.datn.repository;

import com.example.datn.dto.response.HoaDonChiTietResponse;
import com.example.datn.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonChiTietRepo extends JpaRepository<HoaDonChiTiet,Integer> {
    @Query("SELECT new com.example.datn.dto.response.HoaDonChiTietResponse(" +
            "hd.ma, sp.ten, spct.hinh_anh, hdct.soLuong, hdct.gia, hdct.thanhTien) " +
            "FROM HoaDonChiTiet hdct " +
            "JOIN hdct.hoaDon hd " +
            "JOIN hdct.sanPhamChiTiet spct " +
            "JOIN spct.sanPham sp " +
            "WHERE hd.id = :hoaDonId")
    List<HoaDonChiTietResponse> findSanPhamByHoaDonId(@Param("hoaDonId") int hoaDonId);
}
