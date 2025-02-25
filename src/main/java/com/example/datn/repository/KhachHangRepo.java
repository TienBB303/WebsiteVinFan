package com.example.datn.repository;


import com.example.datn.entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.Optional;

public interface KhachHangRepo extends JpaRepository<KhachHang,Integer>{

//    @Query("SELECT kh FROM KhachHang kh WHERE " +
//            "(kh.ten LIKE %:keyword% OR kh.email LIKE %:keyword% OR kh.soDienThoai LIKE %:keyword%) AND " +
//            "(:trangThai IS NULL OR kh.trangThai = :trangThai) AND " +
//            "(:startDate IS NULL OR kh.ngaySinh >= :startDate) AND " +
//            "(:endDate IS NULL OR kh.ngaySinh <= :endDate)")
//    Page<KhachHang> searchKhachHang(
//            @Param("keyword") String keyword,
//            @Param("trangThai") Boolean trangThai,
//            @Param("startDate") Date startDate,
//            @Param("endDate") Date endDate,
//            Pageable pageable
//    );
    @Query("SELECT kh FROM KhachHang kh " +
            "WHERE (LOWER(kh.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(kh.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(kh.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(kh.soDienThoai) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:trangThai IS NULL OR kh.trangThai = :trangThai) ")
    Page<KhachHang> searchKhachHang(String keyword, Boolean trangThai, Pageable pageable);

    @Query("SELECT kh FROM KhachHang kh " +
            "WHERE (LOWER(kh.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(kh.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(kh.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(kh.soDienThoai) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<KhachHang> searchKhachHangKhongCoTrangThai(String keyword, Pageable pageable);

    KhachHang findBySoDienThoai(String soDienThoai);
    Optional<KhachHang> findByEmail(String email);
    Optional<KhachHang> findByResetToken(String resetToken);

    Optional<KhachHang> findById(Long id);

    default KhachHang profileKhachHang() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Tìm khách hàng theo email và kiểm tra trạng thái
        return findByEmail(email)
                .filter(KhachHang::getTrangThai) // Chỉ trả về khách hàng nếu trạng thái hoạt động
                .orElse(null); // Trả về null nếu tài khoản bị vô hiệu hóa
    }

    @Query("SELECT MAX(kh.ma) FROM KhachHang kh WHERE kh.ma LIKE 'KH%'")
    String findMaxCode();
}