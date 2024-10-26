package com.example.datn.repository;


import com.example.datn.entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface KhachHangRepo extends JpaRepository<KhachHang,Integer>{

    @Query("SELECT kh FROM KhachHang kh WHERE " +
            "(kh.ten LIKE %:keyword% OR kh.email LIKE %:keyword% OR kh.soDienThoai LIKE %:keyword%) AND " +
            "(:trangThai IS NULL OR kh.trangThai = :trangThai) AND " +
            "(:startDate IS NULL OR kh.ngaySinh >= :startDate) AND " +
            "(:endDate IS NULL OR kh.ngaySinh <= :endDate)")
    Page<KhachHang> searchKhachHang(
            @Param("keyword") String keyword,
            @Param("trangThai") Boolean trangThai,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );
    KhachHang findBySoDienThoai(String soDienThoai);
    Optional<KhachHang> findByEmail(String email);

}