package com.example.datn.repository;


import com.example.datn.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Date;

public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    @Query("SELECT nv FROM NhanVien nv WHERE " +
            "(nv.ten LIKE %:keyword% OR nv.email LIKE %:keyword% OR nv.soDienThoai LIKE %:keyword%) AND " +
            "(:trangThai IS NULL OR nv.trangThai = :trangThai) AND " +
            "(:startDate IS NULL OR nv.ngaySinh >= :startDate) AND " +
            "(:endDate IS NULL OR nv.ngaySinh <= :endDate)")
    Page<NhanVien> searchNhanVien(
            @Param("keyword") String keyword,
            @Param("trangThai") Boolean trangThai,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );
}