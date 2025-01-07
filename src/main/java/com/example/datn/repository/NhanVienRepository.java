package com.example.datn.repository;


import com.example.datn.entity.NhanVien;
import com.example.datn.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    Optional<NhanVien> findByResetToken(String resetToken);


    default NhanVien profileNhanVien() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return findByEmail(email).orElse(null);
    }

    @Query("SELECT nv FROM NhanVien nv " +
            "WHERE (LOWER(nv.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(nv.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(nv.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(nv.soDienThoai) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            "OR LOWER(nv.chucVu.viTri) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "AND (:trangThai IS NULL OR nv.trangThai = :trangThai) ")
    Page<NhanVien> searchNhanVien(String keyword, Boolean trangThai, Pageable pageable);

    @Query("SELECT nv FROM NhanVien nv " +
            "WHERE (LOWER(nv.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(nv.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(nv.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(nv.soDienThoai) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<NhanVien> searchNhanVienKhongCoTrangThai(String keyword, Pageable pageable);

    @Query("SELECT nv FROM NhanVien nv " +
            "WHERE LOWER(nv.email) = LOWER(:email) " +
            "OR LOWER(nv.soDienThoai) = LOWER(:sdt) ")
    Boolean findTonTaiEmailvaSdt(String email, String sdt);

    boolean existsByEmail(String email);

    boolean existsBySoDienThoai(String soDienThoai);

    Optional<NhanVien> findByEmail(String email);

    @Query("SELECT MAX(nv.ma) FROM NhanVien nv WHERE nv.ma LIKE 'NV%'")
    String findMaxCode();

    @Query("SELECT nv FROM NhanVien nv WHERE nv.chucVu.viTri = :tenChucVu")
    List<NhanVien> findAllByChucVuTen(String tenChucVu);

    @Query("SELECT nv FROM NhanVien nv WHERE nv.chucVu.id = 1 AND nv.trangThai = true")
    List<NhanVien> quanLyHoatDong();

}